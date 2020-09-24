package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.ObjectToken

interface LoginListener : AuthenticationListener{
    fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, loginDto: DeviceInfo) {}
    fun onLoginFailure(payload: MethodRequestPayload, entity: AuthenticatedEntity, loginDto: DeviceInfo, error: String) {}
}