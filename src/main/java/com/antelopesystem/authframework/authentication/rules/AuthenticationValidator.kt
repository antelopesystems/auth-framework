package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo

interface AuthenticationValidator {
    fun validate(entity: Entity, deviceInfo: DeviceInfo): Int
}