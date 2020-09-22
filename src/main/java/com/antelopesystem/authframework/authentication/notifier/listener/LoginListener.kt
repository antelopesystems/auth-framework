package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
    fun onLoginFailure(payload: MethodRequestPayload, entity: AuthenticatedEntity, loginDto: UserLoginDTO, error: String) {}
}