package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.AuthToken

interface AuthenticationPostProcessor {
    fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken)
    fun onLoginFailure(payload: MethodRequestPayload, method: EntityAuthenticationMethod, error: String)
    fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken)
    fun onRegistrationFailure(payload: MethodRequestPayload, error: String)
    fun onForgotPasswordInitialized(token: String, method: EntityAuthenticationMethod)
    fun onForgotPasswordSuccess(method: EntityAuthenticationMethod)
}