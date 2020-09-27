package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.token.model.*
import javax.servlet.http.HttpServletRequest

interface TokenHandler {
    fun <T: TokenRequest> generateToken(payload: T): Pair<TokenResponse, AuthToken>

    fun getToken(token: String, entityType: String): AuthToken

    fun deleteAllTokensById(entityId: Long, entityType: String)

    fun isTokenPresent(request: HttpServletRequest): Boolean

    fun isCurrentTokenPresent(): Boolean

    fun getCurrentToken(): AuthToken

    fun getTokenFromRequest(request: HttpServletRequest): AuthToken

    fun deleteCurrentToken()
}
