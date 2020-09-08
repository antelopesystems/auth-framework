package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity) {}
    fun onLoginFailure(payload: MethodRequestPayload, entity: AuthenticatedEntity, error: String) {}
}