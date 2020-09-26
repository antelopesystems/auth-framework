package com.antelopesystem.authframework.authentication.rules.anomaly.base

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.rules.DeviceAnomalyType
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface DeviceAnomalyHandler {

    @get:ComponentMapKey
    val anomalyType: DeviceAnomalyType

    fun handle(entity: Entity, deviceInfo: DeviceInfo): Boolean
}