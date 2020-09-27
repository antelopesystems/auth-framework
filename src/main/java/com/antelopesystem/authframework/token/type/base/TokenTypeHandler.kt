package com.antelopesystem.authframework.token.type.base

import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType
import javax.servlet.http.HttpServletRequest

interface TokenTypeHandler {

    @get:ComponentMapKey
    val type: TokenType

    fun getTokenFromRequest(request: HttpServletRequest): AuthToken?

    fun <T : TokenRequest> generateToken(authToken: AuthToken, payload: T): String

    fun isTokenPresent(request: HttpServletRequest): Boolean
}