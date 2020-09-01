package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload

interface AuthenticationNotifier {
    fun onLoginSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity)
    fun onLoginFailure(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity, error: String)
    fun onRegistrationSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity)
    fun onRegistrationFailure(payload: AuthenticationRequestPayload, error: String)
}