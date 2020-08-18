package com.antelopesystem.authframework.auth.type

import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.authframework.auth.model.TokenRequest
import com.antelopesystem.authframework.auth.type.base.TokenTypeHandler
import com.antelopesystem.authframework.auth.type.enums.TokenType
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

    override fun getTokenFromRequest(request: HttpServletRequest): ObjectToken? {
        val authTokenString = request.getHeader(AUTH_TOKEN_NAME)

        return crudHandler.showBy(where {
            "token" Equal authTokenString
            "tokenType" Equal type
        }, ObjectToken::class.java)
                .fromCache()
                .execute()
    }

    override fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String {
        return objectToken.token
    }

    override fun isTokenPresent(request: HttpServletRequest): Boolean {
        return request.getHeader(AUTH_TOKEN_NAME) != null
    }

    companion object {
        const val AUTH_TOKEN_NAME = "auth_token"
    }
}