package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.ObjectToken

interface AuthenticationPostProcessor {
    fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken)
    fun onLoginFailure(payload: MethodRequestPayload, entity: AuthenticatedEntity, error: String)
    fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken)
    fun onRegistrationFailure(payload: MethodRequestPayload, error: String)
    fun onForgotPasswordInitialized(token: String, entity: AuthenticatedEntity)
    fun onForgotPasswordSuccess(entity: AuthenticatedEntity)
}