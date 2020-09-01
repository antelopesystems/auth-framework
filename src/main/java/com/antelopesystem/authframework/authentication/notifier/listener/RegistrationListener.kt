package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity) {}
    fun onRegistrationFailure(payload: AuthenticationRequestPayload, error: String) {}
}