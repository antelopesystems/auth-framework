package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.ObjectToken

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, token: ObjectToken, deviceInfo: DeviceInfo) {}
    fun onLoginFailure(payload: MethodRequestPayload, method: EntityAuthenticationMethod, deviceInfo: DeviceInfo, error: String) {}
}