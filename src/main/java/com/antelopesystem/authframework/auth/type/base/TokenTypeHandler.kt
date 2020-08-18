package com.antelopesystem.authframework.auth.type.base

import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.authframework.auth.model.TokenRequest
import com.antelopesystem.authframework.auth.type.enums.TokenType
import javax.servlet.http.HttpServletRequest

interface TokenTypeHandler {

    @get:ComponentMapKey
    val type: TokenType

    fun getTokenFromRequest(request: HttpServletRequest): ObjectToken?

    fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String

    fun isTokenPresent(request: HttpServletRequest): Boolean
}