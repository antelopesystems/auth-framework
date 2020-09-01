package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity) {}
    fun onLoginFailure(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity, error: String) {}
}