package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity

interface ForgotPasswordListener : AuthenticationListener {
    fun onForgotPasswordInitialized(token: String, entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
    fun onForgotPasswordSuccess(entity: AuthenticatedEntity, loginDto: UserLoginDTO) {}
}