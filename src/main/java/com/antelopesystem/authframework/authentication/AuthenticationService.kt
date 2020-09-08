package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.TokenResponse
import com.antelopesystem.authframework.token.type.enums.TokenType

interface AuthenticationService {
    fun initializeLogin(payload: MethodRequestPayload, tokenType: TokenType)
    fun doLogin(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse
    fun initializeRegistration(payload: MethodRequestPayload, tokenType: TokenType)
    fun doRegister(payload: MethodRequestPayload, tokenType: TokenType): TokenResponse
}