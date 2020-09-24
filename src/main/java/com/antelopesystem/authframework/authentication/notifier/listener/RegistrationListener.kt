package com.antelopesystem.authframework.authentication.notifier.listener

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.model.ObjectToken

interface RegistrationListener : AuthenticationListener{
    fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, loginDto: DeviceInfo) {}
    fun onRegistrationFailure(payload: MethodRequestPayload, loginDto: DeviceInfo, error: String) {}
}

