package com.antelopesystem.authframework.token.type.base

import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.token.model.Token
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType
import javax.servlet.http.HttpServletRequest

interface TokenTypeHandler {

    @get:ComponentMapKey
    val type: TokenType

    fun getTokenFromRequest(request: HttpServletRequest): Token?

    fun <T : TokenRequest> generateToken(token: Token, payload: T): String

    fun isTokenPresent(request: HttpServletRequest): Boolean
}