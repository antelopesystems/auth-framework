package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.Token

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, token: Token, deviceInfo: DeviceInfo) {}
    fun onRegistrationFailure(payload: MethodRequestPayload, deviceInfo: DeviceInfo, error: String) {}
}

