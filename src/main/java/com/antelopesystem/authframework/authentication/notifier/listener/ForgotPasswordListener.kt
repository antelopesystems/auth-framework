package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity

interface ForgotPasswordListener : AuthenticationListener {
    fun onForgotPasswordInitialized(token: String, entity: AuthenticatedEntity, loginDto: DeviceInfo) {}
    fun onForgotPasswordSuccess(entity: AuthenticatedEntity, loginDto: DeviceInfo) {}
}