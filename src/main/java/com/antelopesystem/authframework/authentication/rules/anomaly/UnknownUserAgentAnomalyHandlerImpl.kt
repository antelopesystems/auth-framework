package com.antelopesystem.authframework.authentication.rules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.rules.DeviceAnomalyType
import com.antelopesystem.authframework.authentication.rules.anomaly.base.DeviceAnomalyHandler
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownUserAgentAnomalyHandlerImpl : DeviceAnomalyHandler {

    @Autowired
    private lateinit var entityDeviceHandler: EntityDeviceHandler

    override val anomalyType: DeviceAnomalyType
        get() = DeviceAnomalyType.UNKNOWN_USER_AGENT

    override fun handle(entity: Entity, deviceInfo: DeviceInfo): Boolean {
        val knownUserAgents: List<String> = entityDeviceHandler.getEntityKnownUserAgents(entity.id)
        if(knownUserAgents.isNullOrEmpty()) {
            return false
        }

        if(deviceInfo.userAgent.isNullOrBlank()) {
            return true
        }

        return !knownUserAgents.contains(deviceInfo.userAgent)
    }
}