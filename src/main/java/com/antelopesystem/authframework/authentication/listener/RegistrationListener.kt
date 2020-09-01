package com.antelopesystem.authframework.authentication.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity) {}
    fun onRegistrationFailure(payload: AuthenticationPayload, error: String) {}
}