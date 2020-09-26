package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.token.model.*
import javax.servlet.http.HttpServletRequest

interface TokenHandler {
    fun <T: TokenRequest> generateToken(payload: T): Pair<TokenResponse, Token>

    fun getToken(token: String, entityType: String): Token

    fun deleteAllTokensById(entityId: Long, entityType: String)

    fun isTokenPresent(request: HttpServletRequest): Boolean

    fun isCurrentTokenPresent(): Boolean

    fun getCurrentToken(): Token

    fun getTokenFromRequest(request: HttpServletRequest): Token

    fun deleteCurrentToken()
}
