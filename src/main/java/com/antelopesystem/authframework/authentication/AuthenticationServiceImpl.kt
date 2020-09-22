package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.*
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifier
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.*
import com.antelopesystem.authframework.token.model.request.FingerprintedTimestampTokenRequest
import com.antelopesystem.authframework.token.model.request.LegacyTokenRequest
import com.antelopesystem.authframework.token.model.request.PFTTokenRequest
import com.antelopesystem.authframework.token.model.request.TimestampTokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap

// todo: Logging
class AuthenticationServiceImpl(
        private val tokenHandler: TokenHandler,
        private val authenticationNotifier: AuthenticationNotifier,
        private val crudHandler: CrudHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationService {

    private val setups = mutableMapOf<UserPair, CustomParamsDTO>()

    @ComponentMap
    private lateinit var authenticationMethodHandlers: Map<AuthenticationMethod, AuthenticationMethodHandler>

    override fun initializeRegistration(payload: MethodRequestPayload, tokenType: TokenType): CustomParamsDTO {
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        methodHandler.getEntityMethod(payload)?.let {
            if(settings.allowLoginOnRegistration) {
                return initializeLogin(payload, tokenType)
            }
            throw RegistrationFailedException("Entity already exists")
        }

        val username = methodHandler.getUsernameFromPayload(payload)
        val params = methodHandler.initializeRegistration(payload)
        setups[UserPair(username, methodHandler.method)] = params
        return params
    }

    override fun doRegister(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse {
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        try {
            val username = methodHandler.getUsernameFromPayload(payload)
            methodHandler.getEntityMethod(payload)?.let {
                if(settings.allowLoginOnRegistration) {
                    return doLogin(payload, tokenType)
                }
                throw RegistrationFailedException("Entity already exists")
            }

            val params = setups[UserPair(username, methodHandler.method)] ?: throw RegistrationFailedException("Registration was not initialized")
            var entity = AuthenticatedEntity(type = payload.type)
            val method = methodHandler.doRegister(payload, params, entity)
            entity.authenticationMethods.add(method)
            entity = crudHandler.create(entity).execute()
            authenticationNotifier.onRegistrationSuccess(payload, entity)

            val request = buildTokenRequest(tokenType, method, payload.parameters)
            return tokenHandler.generateToken(request)
        } catch(e: AuthenticationMethodException) {
            authenticationNotifier.onRegistrationFailure(payload, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onRegistrationFailure(payload, UNHANDLED_EXCEPTION)
            throw RegistrationFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeLogin(payload: MethodRequestPayload, tokenType: TokenType): CustomParamsDTO {
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        val method = methodHandler.getEntityMethod(payload)
        if(method == null) {
            if(settings.allowRegistrationOnLogin) {
                return initializeRegistration(payload, tokenType)
            }
            error(ENTITY_NOT_FOUND)
        }
        return methodHandler.initializeLogin(payload, method)
    }

    override fun doLogin(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse {
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        val method = methodHandler.getEntityMethod(payload)
        if(method == null) {
            if(settings.allowRegistrationOnLogin) {
                return doRegister(payload, tokenType)
            }
            error(ENTITY_NOT_FOUND)
        }

        try {
            methodHandler.doLogin(payload, method)
            authenticationNotifier.onLoginSuccess(payload, method.entity)
            val request = buildTokenRequest(tokenType, method, payload.parameters)
            return tokenHandler.generateToken(request)
        } catch(e: AuthenticationMethodException) {
            authenticationNotifier.onLoginFailure(payload, method.entity, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onLoginFailure(payload, method.entity, UNHANDLED_EXCEPTION)
            throw LoginFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeForgotPassword(payload: MethodRequestPayload) {
        val methodHandler = getMethodHandler(payload)
        if(!methodHandler.isPasswordBased()) {
            throw error("Method [ ${methodHandler.method} ] is not supported")
        }

        val method = methodHandler.getEntityMethod(payload) ?: error(ENTITY_NOT_FOUND)

        val token = crudHandler.create(ForgotPasswordToken(method)).execute()
        authenticationNotifier.onForgotPasswordInitialized(token.token, method.entity)
    }

    override fun redeemForgotPasswordToken(tokenString: String, newPassword: String, objectType: String) {
        val token = crudHandler.showBy(where {
            "token" Equal tokenString
        }, ForgotPasswordToken::class.java)
                .execute()
                ?: error("Invalid token")

        val method = token.entityMethod
        val methodHandler = getMethodHandlerByType(method.method, objectType)

        if(!methodHandler.isPasswordBased()) {
            throw error("Method [ ${methodHandler.method} ] is not supported")
        }

        methodHandler.changePassword(newPassword, method)
        crudHandler.update(method).execute()
        crudHandler.delete(token.id, ForgotPasswordToken::class.java).execute()
        authenticationNotifier.onForgotPasswordSuccess(method.entity)
    }

    override fun changePassword(payload: MethodRequestPayload, newPassword: String, objectType: String) {
        val methodHandler = getMethodHandler(payload)
        val method = methodHandler.getEntityMethod(payload) ?: error(ENTITY_NOT_FOUND)
        if(!methodHandler.checkPassword(payload, method)) {
            error("Old password does not match")
        }

        methodHandler.changePassword(newPassword, method)
        if(tokenHandler.isCurrentTokenPresent()) {
            val token = tokenHandler.getCurrentToken()
            token.passwordChangeRequired = false
            crudHandler.update(token).execute()
        }
    }

    private fun validateTokenType(payload: MethodRequestPayload, tokenType: TokenType) {
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        if(!settings.getAllowedTokenTypeEnums().contains(tokenType)) {
            error("Unsupported token type")
        }
    }

    private fun getMethodHandler(payload: MethodRequestPayload): AuthenticationMethodHandler {
        val methodHandler = authenticationMethodHandlers.values.find { it.isSupportedForPayload(payload) } ?: throw error("Not suitable method found")

        if(!methodHandler.isSupportedForType(payload.type)) {
            error("Method [ ${methodHandler.method} ] is not supported")
        }

        return methodHandler
    }

    private fun getMethodHandlerByType(method: AuthenticationMethod, type: String): AuthenticationMethodHandler {
        val methodHandler = authenticationMethodHandlers[method] ?: throw error("Not suitable method found")

        if(!methodHandler.isSupportedForType(type)) {
            error("Method [ ${methodHandler.method} ] is not supported")
        }

        return methodHandler
    }

    private fun buildTokenRequest(type: TokenType, method: EntityAuthenticationMethod, parameters: Map<String, Any>): TokenRequest {
        val entity = method.entity
        val methodHandler = getMethodHandlerByType(method.method, entity.type)
        var passwordChangeRequired = false
        if(methodHandler.isPasswordBased()) {
            passwordChangeRequired = methodHandler.isPasswordExpired(method)
        }

        val mfaRequired = entity.mfaMethods.isNotEmpty()

        return when(type) {
            TokenType.Legacy -> LegacyTokenRequest(
                    entity.id,
                    entity.type,
                    "ip", //todo
                    passwordChangeRequired,
                    mfaRequired
            )
            TokenType.Timestamp -> TimestampTokenRequest(
                    entity.id,
                    entity.type,
                    "ip",
                    parameters["publicKey"].toString(),
                    passwordChangeRequired,
                    mfaRequired
            )
            TokenType.FingerprintedTimestamp -> FingerprintedTimestampTokenRequest(
                    entity.id,
                    entity.type,
                    "ip",
                    parameters["publicKey"].toString(),
                    parameters["fingerprint"].toString(),
                    passwordChangeRequired,
                    mfaRequired
            )
            TokenType.PFT -> PFTTokenRequest(
                    entity.id,
                    entity.type,
                    "ip",
                    parameters["publicKey"].toString(),
                    parameters["fingerprint"].toString(),
                    passwordChangeRequired,
                    mfaRequired
            )
        }
    }

    companion object {
        private val ENTITY_NOT_FOUND = "Entity not found"
        private val UNHANDLED_EXCEPTION = "Unhandled exception"
    }
}

data class UserPair(val username: String, val method: AuthenticationMethod)