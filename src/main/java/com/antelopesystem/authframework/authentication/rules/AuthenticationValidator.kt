package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo

interface AuthenticationValidator {
    fun validate(entity: AuthenticatedEntity, deviceInfo: DeviceInfo): Int
}