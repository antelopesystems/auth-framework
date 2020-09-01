package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.authframework.token.model.TokenResponse

interface AuthenticationService {
    fun initializeLogin(payload: AuthenticationPayload)
    fun doLogin(payload: AuthenticationPayload): TokenResponse
    fun initializeRegistration(payload: AuthenticationPayload)
    fun doRegister(payload: AuthenticationPayload): TokenResponse
}