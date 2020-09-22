package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
    fun onRegistrationFailure(payload: MethodRequestPayload, loginDto: UserLoginDTO, error: String) {}
}

interface ForgotPasswordListener : AuthenticationListener {
    fun onForgotPasswordInitialized(token: String, entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
    fun onForgotPasswordSuccess(entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
}