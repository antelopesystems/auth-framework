package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.Token

interface AuthenticationPostProcessor {
    fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, token: Token)
    fun onLoginFailure(payload: MethodRequestPayload, method: EntityAuthenticationMethod, error: String)
    fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, token: Token)
    fun onRegistrationFailure(payload: MethodRequestPayload, error: String)
    fun onForgotPasswordInitialized(token: String, method: EntityAuthenticationMethod)
    fun onForgotPasswordSuccess(method: EntityAuthenticationMethod)
}