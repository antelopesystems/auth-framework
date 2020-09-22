package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.exception.InvalidTokenException
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.model.TokenResponse
import com.antelopesystem.authframework.token.type.base.TokenTypeHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class TokenHandlerImpl : TokenHandler {

    @ComponentMap
    private lateinit var authenticationTypeHandlers: Map<TokenType, TokenTypeHandler>

    @Autowired
    private lateinit var crudHandler: CrudHandler

    @Autowired
    private lateinit var securitySettingsHandler: SecuritySettingsHandler

    @Autowired
    private lateinit var request: HttpServletRequest

    override fun getToken(token: String, objectType: String): ObjectToken {
        return crudHandler.showBy(where {
            "token" Equal token
            "objectType" Equal objectType

        }, ObjectToken::class.java)
                .fromCache()
                .execute() ?: throw InvalidTokenException()
    }

    override fun getTokenFromRequest(request: HttpServletRequest): ObjectToken {
        val typeHandler = getAuthenticationTypeHandler(request)
        val token = typeHandler.getTokenFromRequest(request) ?: throw InvalidTokenException()
        if(token.expiryTime!!.before(Date())) {
            log.info("Attempted usage of expired token (ID: ${token.id})")
            throw InvalidTokenException()
        }
        return token;
    }


    override fun getCurrentToken(): ObjectToken = getTokenFromRequest(request)

    override fun deleteCurrentToken() {
        try {
            val token = getCurrentToken()
            if(token.immutable) return
            crudHandler.delete(token.id, ObjectToken::class.java).execute()

        } catch(e: Exception) {
            when(e) {
                is InvalidTokenException -> {}
                else -> throw e
            }
        }
    }

    override fun <T : TokenRequest> generateToken(payload: T): TokenResponse {
        // todo. add settings such as supported token types for object etc.
        val securitySettings = securitySettingsHandler.getSecuritySettings(payload.objectType)
        val typeHandler = authenticationTypeHandlers[payload.type]

        val objectToken = ObjectToken(
                tokenType = payload.type,
                objectId = payload.objectId,
                objectType = payload.objectType,
                ip = payload.ip,
                passwordChangeRequired = payload.passwordChangeRequired,
                mfaRequired = payload.mfaRequired,
                expiryTime = Date(System.currentTimeMillis() + securitySettings.tokenLifetimeHours * 60L * 60L * 1000L)
        )

        val processedToken = typeHandler!!.generateToken(objectToken, payload)

        crudHandler.create(objectToken)
                .execute()

        return TokenResponse(processedToken, objectToken.sessionId, payload.type)
    }

    override fun deleteAllTokensById(objectId: Long, objectType: String) {
        val tokens = crudHandler.index(where {
            "objectId" Equal objectId
            "objectType" Equal objectType
            "immutable" Equal false
        }, ObjectToken::class.java).execute()

        for (token in tokens.data) {
            crudHandler.delete(token.id, ObjectToken::class.java).execute()
        }
    }

    override fun isCurrentTokenPresent(): Boolean {
        if (RequestContextHolder.getRequestAttributes() != null) {
            return isTokenPresent(request)
        }

        return false
    }


    override fun isTokenPresent(request: HttpServletRequest) : Boolean {
        val typeHandler = getAuthenticationTypeHandler(request)

        return typeHandler.isTokenPresent(request)
    }

    private fun getAuthenticationTypeHandler(request: HttpServletRequest): TokenTypeHandler {
        var authType: TokenType = TokenType.Legacy

        val authTypeString = request.getHeader("x-auth-type")
        if (!authTypeString.isNullOrBlank()) {
            try {
                authType = TokenType.valueOf(authTypeString)
            } catch (e: Exception){}
        }

        return authenticationTypeHandlers.getValue(authType)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TokenHandlerImpl::class.java)
    }
}