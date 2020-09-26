package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod

interface ForgotPasswordListener : AuthenticationListener {
    fun onForgotPasswordInitialized(token: String, method: EntityAuthenticationMethod, deviceInfo: DeviceInfo) {}
    fun onForgotPasswordSuccess(method: EntityAuthenticationMethod, deviceInfo: DeviceInfo) {}
}