package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.TokenResponse
import com.antelopesystem.authframework.token.type.enums.TokenType

interface AuthenticationService {
    fun initializeLogin(payload: MethodRequestPayload, tokenType: TokenType): Any?
    fun doLogin(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse
    fun initializeRegistration(payload: MethodRequestPayload, tokenType: TokenType): CustomParamsDTO
    fun doRegister(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse
    fun initializeForgotPassword(payload: MethodRequestPayload)
    fun redeemForgotPasswordToken(tokenString: String, newPassword: String, objectType: String)
    fun changePassword(payload: MethodRequestPayload, newPassword: String, objectType: String)
}