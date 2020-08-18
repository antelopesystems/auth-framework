package com.antelopesystem.authframework.auth

import com.antelopesystem.authframework.auth.model.*
import javax.servlet.http.HttpServletRequest

interface OperatorAuthenticationHandler {

    fun getTokenFromRequest(request: HttpServletRequest): ObjectToken

    fun <T: TokenRequest> generateToken(payload: T): TokenResponse

    fun deleteCurrentToken()

    fun isTokenPresent(request: HttpServletRequest): Boolean

    fun getToken(token: String, objectType: String): ObjectToken

    fun deleteAllTokensById(id: Long, objectType: String)

    fun isCurrentTokenPresent(): Boolean

    fun getCurrentToken(): ObjectToken

    fun getOriginalOperatorFromCurrentToken(): Long

    fun deleteOldTokens()

    fun addTokenForExtendedValidation(token: ObjectToken)

    fun isTokenExtendedValidationActive(token: ObjectToken): Boolean
    fun requireTotp(): Boolean
    fun requireTotpAlways(): Boolean

    /**
     * Update password on operator
     *
     * @param user
     */
    fun updatePassword(operatorId: Long, newPassword: String)

    fun encodePassword(rawPassword: String): String

    fun isPasswordInOldEncoding(encodedPassword: String): Boolean

    fun isPasswordValidForOperator(rawPassword: String, encodedPassword: String, passwordSalt: String?): Boolean
}