package com.antelopesystem.authframework.token.type.base

import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType
import javax.servlet.http.HttpServletRequest

interface TokenTypeHandler {

    @get:ComponentMapKey
    val type: TokenType

    fun getTokenFromRequest(request: HttpServletRequest): ObjectToken?

    fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String

    fun isTokenPresent(request: HttpServletRequest): Boolean
}