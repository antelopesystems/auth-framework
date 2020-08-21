package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.token.model.*
import javax.servlet.http.HttpServletRequest

interface TokenHandler {


    fun <T: TokenRequest> generateToken(payload: T): TokenResponse

    fun getToken(token: String, objectType: String): ObjectToken

    fun deleteAllTokensById(objectId: Long, objectType: String)

    fun isTokenPresent(request: HttpServletRequest): Boolean

    fun isCurrentTokenPresent(): Boolean

    fun getCurrentToken(): ObjectToken

    fun getTokenFromRequest(request: HttpServletRequest): ObjectToken

    fun deleteCurrentToken()
}
