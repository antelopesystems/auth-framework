package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.*
import com.antelopesystem.authframework.authentication.notifier.AuthenticationPostProcessor
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.*
import com.antelopesystem.authframework.token.model.request.FingerprintedTimestampTokenRequest
import com.antelopesystem.authframework.token.model.request.LegacyTokenRequest
import com.antelopesystem.authframework.token.model.request.PFTTokenRequest
import com.antelopesystem.authframework.token.model.request.TimestampTokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.authframework.util.error
import com.antelopesystem.authframework.util.forLog
import com.antelopesystem.authframework.util.trace
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// todo: Logging
@Component
class AuthenticationServiceImpl(
        private val tokenHandler: TokenHandler,
        private val authenticationPostProcessor: AuthenticationPostProcessor,
        private val crudHandler: CrudHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationService {

    private val setups = mutableMapOf<UserPair, CustomParamsDTO>()

    @ComponentMap
    private lateinit var authenticationMethodHandlers: Map<AuthenticationMethod, AuthenticationMethodHandler>

    override fun initializeRegistration(payload: MethodRequestPayload, tokenType: TokenType): CustomParamsDTO {
        log.trace { "Received initializeRegistration with payload: {$payload}, tokenType: $tokenType" }
        validateTokenType(payload, tokenType)

        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        methodHandler.getEntityMethod(payload)?.let {
            if(settings.allowLoginOnRegistration) {
                log.trace { "${it.forLog()} exists and allowLoginOnRegistration=true, switching to initializeLogin" }
                return initializeLogin(payload, tokenType)
            }
            log.trace { "${it.forLog()} exists and login is not allowed on registration, terminating" }
            throw RegistrationFailedException("Entity already exists")
        }

        val username = methodHandler.getUsernameFromPayload(payload)
        val params = methodHandler.initializeRegistration(payload)
        setups[UserPair(username, methodHandler.method)] = params
        log.trace { "Initialized registration for username: $username, params: $params, method: ${methodHandler.method}" }
        return params
    }

    override fun doRegister(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse {
        log.trace { "Received doRegister with payload: {$payload}, tokenType: $tokenType" }
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        try {
            val username = methodHandler.getUsernameFromPayload(payload)
            methodHandler.getEntityMethod(payload)?.let {
                if(settings.allowLoginOnRegistration) {
                    log.trace { "${it.forLog()} exists and allowLoginOnRegistration=true, switching to doLogin" }
                    return doLogin(payload, tokenType)
                }
                log.trace { "${it.forLog()} exists and login is not allowed on registration, terminating" }
                throw RegistrationFailedException(ENTITY_NOT_FOUND)
            }

            val params = setups[UserPair(username, methodHandler.method)] ?: throw RegistrationFailedException("Registration was not initialized")
            var entity = AuthenticatedEntity(type = payload.type)
            val method = methodHandler.doRegister(payload, params, entity)
            method.primary = true
            entity.authenticationMethods.add(method)
            entity = crudHandler.create(entity).execute()
            log.trace { "Performed registration for ${method.forLog()}, params: [ $params ]" }
            val request = buildTokenRequest(tokenType, method, payload.parameters)
            val (tokenResponse, token) = tokenHandler.generateToken(request)
            authenticationPostProcessor.onRegistrationSuccess(payload, method, token)
            return tokenResponse
        } catch(e: AuthenticationMethodException) {
            authenticationPostProcessor.onRegistrationFailure(payload, e.message.toString())
            log.info("Registration failed: ${e.message}")
            throw e
        } catch(e: Exception) {
            authenticationPostProcessor.onRegistrationFailure(payload, UNHANDLED_EXCEPTION)
            log.error(e) { "Registration failed" }
            throw RegistrationFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeLogin(payload: MethodRequestPayload, tokenType: TokenType): CustomParamsDTO {
        log.trace { "Received initializeLogin with payload: {$payload}, tokenType: $tokenType" }
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        val method = methodHandler.getEntityMethod(payload)
        if(method == null) {
            if(settings.allowRegistrationOnLogin) {
                log.trace { "Username [ ${methodHandler.getUsernameFromPayload(payload)} ] for method [ ${methodHandler.method} ] does not exist and allowRegistrationOnLogin=true, switching to initializeRegistration" }
                return initializeRegistration(payload, tokenType)
            }
            log.trace { "Username [ ${methodHandler.getUsernameFromPayload(payload)} ] for method [ ${methodHandler.method} ] does not exist and allowRegistrationOnLogin=false, terminating" }
            error(ENTITY_NOT_FOUND)
        }
        return methodHandler.initializeLogin(payload, method)
    }

    override fun doLogin(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse {
        log.trace { "Received initializeLogin with payload: {$payload}, tokenType: $tokenType" }
        validateTokenType(payload, tokenType)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload)
        val method = methodHandler.getEntityMethod(payload)
        if(method == null) {
            if(settings.allowRegistrationOnLogin) {
                log.trace { "Username [ ${methodHandler.getUsernameFromPayload(payload)} ] for method [ ${methodHandler.method} ] does not exist and allowRegistrationOnLogin=true, switching to initializeRegistration" }
                return doRegister(payload, tokenType)
            }
            log.trace { "Username [ ${methodHandler.getUsernameFromPayload(payload)} ] for method [ ${methodHandler.method} ] does not exist and allowRegistrationOnLogin=false, terminating" }
            error(ENTITY_NOT_FOUND)
        }

        try {
            log.trace { "Performing login request for ${method.forLog()}, method: [ ${methodHandler.method} ]" }
            methodHandler.doLogin(payload, method)
            log.trace { "Performed login request for ${method.forLog()}, method: [ ${methodHandler.method} ]" }
            val request = buildTokenRequest(tokenType, method, payload.parameters)
            val (tokenResponse, token) = tokenHandler.generateToken(request)
            authenticationPostProcessor.onLoginSuccess(payload, method, token)
            return tokenResponse
        } catch(e: AuthenticationMethodException) {
            authenticationPostProcessor.onLoginFailure(payload, method, e.message.toString())
            log.info("Login failed: ${e.message}")
            throw e
        } catch(e: Exception) {
            authenticationPostProcessor.onLoginFailure(payload, method, UNHANDLED_EXCEPTION)
            log.error(e) { "Login Failed" }
            throw LoginFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeForgotPassword(payload: MethodRequestPayload) {
        log.trace { "Received initializeForgotPassword with payload: {$payload}" }
        val methodHandler = getMethodHandler(payload)
        if(!methodHandler.isPasswordBased()) {
            log.error { "initializeForgotPassword failed for method [ ${methodHandler.method} ] as it is not password based" }
            error("Method [ ${methodHandler.method} ] is not supported")
        }

        val method = methodHandler.getEntityMethod(payload) ?: error(ENTITY_NOT_FOUND)
        val token = crudHandler.create(ForgotPasswordToken(method)).execute()
        authenticationPostProcessor.onForgotPasswordInitialized(token.token, method)
        log.trace { "Performed initializeForgotPassword for ${method.forLog()}: {$payload}" }
    }

    override fun redeemForgotPasswordToken(tokenString: String, newPassword: String, entityType: String) {
        log.trace { "Received redeemForgotPasswordToken with token: [ {$tokenString} ]" }
        val token = crudHandler.showBy(where {
            "token" Equal tokenString
        }, ForgotPasswordToken::class.java)
                .execute()
                ?: error("Invalid token")

        val method = token.entityMethod
        val methodHandler = getMethodHandlerByType(method.method, entityType)

        if(!methodHandler.isPasswordBased()) {
            log.error { "redeemForgotPasswordToken failed for method [ ${methodHandler.method} ] as it is not password based" }
            throw error("Method [ ${methodHandler.method} ] is not supported")
        }

        methodHandler.changePassword(newPassword, method)
        crudHandler.update(method).execute()
        crudHandler.delete(token.id, ForgotPasswordToken::class.java).execute()
        authenticationPostProcessor.onForgotPasswordSuccess(method)
        log.trace { "Performed redeemForgotPasswordToken for ${method.forLog()}" }
    }

    override fun changePassword(payload: MethodRequestPayload, newPassword: String, entityType: String) {
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

        val securitySettings = securitySettingsHandler.getSecuritySettings(payload.type)

        if(!securitySettings.allowedAuthenticationMethods.contains(methodHandler.method) || !methodHandler.isSupportedForType(payload.type)) {
            error("Method [ ${methodHandler.method} ] is not supported")
        }
        return methodHandler
    }

    override fun getAvailableMethods(entityType: String): List<AuthenticationMethodDTO> {
        val securitySettings = securitySettingsHandler.getSecuritySettings(entityType)
        return securitySettings.allowedAuthenticationMethods.map { AuthenticationMethodDTO(it, it == securitySettings.defaultAuthenticationMethod) }
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
        private val log = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
    }
}

data class UserPair(val username: String, val method: AuthenticationMethod)

data class AuthenticationMethodDTO(val method: AuthenticationMethod, val default: Boolean)