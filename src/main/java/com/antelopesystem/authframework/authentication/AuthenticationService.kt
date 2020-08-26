package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.controller.AuthenticationPayload

interface AuthenticationService {
    fun initializeLogin(payload: AuthenticationPayload)
    fun doLogin(payload: AuthenticationPayload)
    fun initializeRegistration(payload: AuthenticationPayload)
    fun doRegister(payload: AuthenticationPayload)
}