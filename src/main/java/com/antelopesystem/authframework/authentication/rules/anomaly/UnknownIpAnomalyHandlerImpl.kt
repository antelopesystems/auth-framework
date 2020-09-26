package com.antelopesystem.authframework.authentication.rules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.rules.DeviceAnomalyType
import com.antelopesystem.authframework.authentication.rules.anomaly.base.DeviceAnomalyHandler
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownIpAnomalyHandlerImpl : DeviceAnomalyHandler {

    @Autowired
    private lateinit var entityDeviceHandler: EntityDeviceHandler

    override val anomalyType: DeviceAnomalyType
        get() = DeviceAnomalyType.UNKNOWN_IP

    override fun handle(entity: Entity, deviceInfo: DeviceInfo): Boolean {
        val knownIps: List<String> = entityDeviceHandler.getEntityKnownIps(entity.id)
        if(knownIps.isNullOrEmpty()) {
            return false
        }

        if(deviceInfo.ip.isNullOrBlank()) {
            return true
        }

        return !knownIps.contains(deviceInfo.ip)
    }
}