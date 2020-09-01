package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifier
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.*
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap

// todo: Logging
class AuthenticationServiceImpl(
        private val tokenHandler: TokenHandler,
        private val authenticationNotifier: AuthenticationNotifier,
        private val crudHandler: CrudHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationService {
    @ComponentMap(key = AuthenticationMethod::class, value = AuthenticationMethodHandler::class)
    private lateinit var authenticationMethodHandlers: Map<AuthenticationMethod, AuthenticationMethodHandler>

    override fun initializeLogin(payload: AuthenticationRequestPayload) {
        validateTokenType(payload)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload.authenticationMethod, payload.type)
        val entity = methodHandler.getEntityMethod(payload)
        if(entity == null) {
            if(settings.allowRegistrationOnLogin) {
                return initializeRegistration(payload)
            }
            error(ENTITY_NOT_FOUND)
        }
        methodHandler.initializeLogin(payload, entity)
    }

    override fun doLogin(payload: AuthenticationRequestPayload): TokenResponse {
        validateTokenType(payload)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload.authenticationMethod, payload.type)
        val method = methodHandler.getEntityMethod(payload)
        if(method == null) {
            if(settings.allowRegistrationOnLogin) {
                return doRegister(payload)
            }
            error(ENTITY_NOT_FOUND)
        }

        try {
            methodHandler.doLogin(payload, method)
            authenticationNotifier.onLoginSuccess(payload, method.entity)
            val request = buildTokenRequest(payload.tokenType, method.entity, payload.bodyMap)
            return tokenHandler.generateToken(request)
        } catch(e: AuthenticationMethodException) {
            authenticationNotifier.onLoginFailure(payload, method.entity, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onLoginFailure(payload, method.entity, UNHANDLED_EXCEPTION)
            throw LoginFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeRegistration(payload: AuthenticationRequestPayload) {
        validateTokenType(payload)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload.authenticationMethod, payload.type)
        methodHandler.getEntityMethod(payload)?.let {
            if(settings.allowLoginOnRegistration) {
                return initializeLogin(payload)
            }
            throw RegistrationFailedException("Entity already exists")
        }
        methodHandler.initializeRegistration(payload)
    }

    override fun doRegister(payload: AuthenticationRequestPayload): TokenResponse {
        validateTokenType(payload)
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        val methodHandler = getMethodHandler(payload.authenticationMethod, payload.type)
        try {
            methodHandler.getEntityMethod(payload)?.let {
                if(settings.allowLoginOnRegistration) {
                    return doRegister(payload)
                }
                throw RegistrationFailedException("Entity already exists")
            }

            var entity = AuthenticatedEntity(type = payload.type)
            val method = methodHandler.doRegister(payload, entity)
            entity.authenticationMethods.add(method)
            entity = crudHandler.create(entity).execute()
            authenticationNotifier.onRegistrationSuccess(payload, entity)

            val request = buildTokenRequest(payload.tokenType, entity, payload.bodyMap)
            return tokenHandler.generateToken(request)
        } catch(e: AuthenticationMethodException) {
            authenticationNotifier.onRegistrationFailure(payload, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onRegistrationFailure(payload, UNHANDLED_EXCEPTION)
            throw RegistrationFailedException(UNHANDLED_EXCEPTION)
        }
    }

    private fun validateTokenType(payload: AuthenticationRequestPayload) {
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        if(!settings.getAllowedTokenTypeEnums().contains(payload.tokenType)) {
            error("Unsupported token type")
        }
    }

    private fun getMethodHandler(method: AuthenticationMethod, type: String): AuthenticationMethodHandler {
        val methodHandler = authenticationMethodHandlers[method] ?: error("Handler for method [ $method ] not found")

        if(!methodHandler.isSupportedForType(type)) {
            error("Method [ $method ] is not supported")
        }

        return methodHandler
    }

    private fun buildTokenRequest(type: TokenType, entity: AuthenticatedEntity, parameters: Map<String, Any>): TokenRequest {
        return when(type) {
            TokenType.Legacy -> LegacyTokenRequest(
                    entity.id,
                    entity.type,
                    "ip", //todo
                    false,
                    false
            )
            TokenType.Timestamp -> TimestampTokenRequest(
                    entity.id,
                    entity.type,
                    "ip",
                    parameters["publicKey"].toString(),
                    false,
                    false
            )
            TokenType.FingerprintedTimestamp -> FingerprintedTimestampTokenRequest(
                    entity.id,
                    entity.type,
                    "ip",
                    parameters["publicKey"].toString(),
                    parameters["fingerprint"].toString(),
                    false,
                    false
            )
        }
    }

    companion object {
        private val ENTITY_NOT_FOUND = "Entity not found"
        private val UNHANDLED_EXCEPTION = "Unhandled exception"
    }
}