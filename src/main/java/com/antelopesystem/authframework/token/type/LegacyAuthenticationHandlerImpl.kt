package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.base.TokenTypeHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class LegacyAuthenticationHandlerImpl : TokenTypeHandler {

    override val type = TokenType.Legacy

    @Autowired
    lateinit var crudHandler: CrudHandler

    override fun getTokenFromRequest(request: HttpServletRequest): AuthToken? {
        val authTokenString = request.getHeader(AUTH_TOKEN_NAME)

        return crudHandler.showBy(where {
            "token" Equal authTokenString
            "tokenType" Equal type
        }, AuthToken::class.java)
                .fromCache()
                .execute()
    }

    override fun <T : TokenRequest> generateToken(authToken: AuthToken, payload: T): String {
        return authToken.token
    }

    override fun isTokenPresent(request: HttpServletRequest): Boolean {
        return request.getHeader(AUTH_TOKEN_NAME) != null
    }

    companion object {
        const val AUTH_TOKEN_NAME = "auth_token"
    }
}