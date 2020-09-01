package com.antelopesystem.authframework.authentication.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity) {}
    fun onLoginFailure(payload: AuthenticationPayload, entity: AuthenticatedEntity, error: String) {}
}