package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity) {}
    fun onRegistrationFailure(payload: MethodRequestPayload, error: String) {}
}