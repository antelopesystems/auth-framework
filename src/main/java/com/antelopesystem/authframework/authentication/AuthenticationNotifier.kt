package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload

interface AuthenticationNotifier {
    fun onLoginSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity)
    fun onLoginFailure(payload: AuthenticationPayload, entity: AuthenticatedEntity, error: String)
    fun onRegistrationSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity)
    fun onRegistrationFailure(payload: AuthenticationPayload, error: String)
}