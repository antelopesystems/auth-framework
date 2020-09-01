package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.*
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap

// todo: Logging, security settings
class AuthenticationServiceImpl(
        private val tokenHandler: TokenHandler,
        private val authenticationNotifier: AuthenticationNotifier,
        private val crudHandler: CrudHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationService {
    @ComponentMap(key = AuthenticationType::class, value = AuthenticationTypeHandler::class)
    private lateinit var authenticationTypeHandlers: Map<AuthenticationType, AuthenticationTypeHandler>

    override fun initializeLogin(payload: AuthenticationPayload) {
        validateTokenType(payload)
        val handler = getTypeProvider(payload.authenticationType, payload.type)
        val entity = handler.getEntity(payload) ?: error(ENTITY_NOT_FOUND)
        handler.initializeLogin(payload, entity)
    }

    override fun doLogin(payload: AuthenticationPayload): TokenResponse {
        validateTokenType(payload)
        val provider = getTypeProvider(payload.authenticationType, payload.type)
        val entity = provider.getEntity(payload) ?: error(ENTITY_NOT_FOUND)
        try {
            provider.doLogin(payload, entity)
            authenticationNotifier.onLoginSuccess(payload, entity)
            val request = buildTokenRequest(payload.tokenType, entity, payload.bodyMap)
            return tokenHandler.generateToken(request)
        } catch(e: LoginFailedException) {
            authenticationNotifier.onLoginFailure(payload, entity, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onLoginFailure(payload, entity, UNHANDLED_EXCEPTION)
            throw LoginFailedException(UNHANDLED_EXCEPTION)
        }
    }

    override fun initializeRegistration(payload: AuthenticationPayload) {
        validateTokenType(payload)
        val handler = getTypeProvider(payload.authenticationType, payload.type)
        handler.initializeRegistration(payload)
    }

    override fun doRegister(payload: AuthenticationPayload): TokenResponse {
        validateTokenType(payload)
        val provider = getTypeProvider(payload.authenticationType, payload.type)
        try {

            provider.getEntity(payload)?.let {
                throw RegistrationFailedException("Entity already exists")
            }

            var entity = provider.doRegister(payload, AuthenticatedEntity(type = payload.type))
            entity = crudHandler.create(entity).execute()
            authenticationNotifier.onRegistrationSuccess(payload, entity)

            val request = buildTokenRequest(payload.tokenType, entity, payload.bodyMap)
            return tokenHandler.generateToken(request)
        } catch(e: RegistrationFailedException) {
            authenticationNotifier.onRegistrationFailure(payload, e.message.toString())
            throw e
        } catch(e: Exception) {
            authenticationNotifier.onRegistrationFailure(payload, UNHANDLED_EXCEPTION)
            throw RegistrationFailedException(UNHANDLED_EXCEPTION)
        }
    }

    private fun validateTokenType(payload: AuthenticationPayload) {
        val settings = securitySettingsHandler.getSecuritySettings(payload.type)
        if(!settings.allowedTokenTypes.contains(payload.tokenType)) {
            error("Unsupported token type")
        }
    }

    private fun getTypeProvider(authenticationType: AuthenticationType, type: String): AuthenticationTypeHandler {
        val provider = authenticationTypeHandlers[authenticationType] ?: error("Provider for authentication type [ $authenticationType ] not found")

        if(!provider.isSupportedForType(type)) {
            error("Authentication type [ $authenticationType ] is not supported")
        }

        return provider
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