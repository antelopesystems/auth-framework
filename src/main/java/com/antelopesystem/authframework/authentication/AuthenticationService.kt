package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import com.antelopesystem.authframework.token.model.TokenResponse

interface AuthenticationService {
    fun initializeLogin(payload: AuthenticationRequestPayload)
    fun doLogin(payload: AuthenticationRequestPayload): TokenResponse
    fun initializeRegistration(payload: AuthenticationRequestPayload)
    fun doRegister(payload: AuthenticationRequestPayload): TokenResponse
}