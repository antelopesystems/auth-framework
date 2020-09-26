package com.antelopesystem.authframework.authentication.rules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.rules.DeviceAnomalyType
import com.antelopesystem.authframework.authentication.rules.anomaly.base.DeviceAnomalyHandler
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownFingerprintAnomalyHandlerImpl : DeviceAnomalyHandler {

    @Autowired
    private lateinit var entityDeviceHandler: EntityDeviceHandler

    override val anomalyType: DeviceAnomalyType
        get() = DeviceAnomalyType.UNKNOWN_FINGERPRINT

    override fun handle(entity: Entity, deviceInfo: DeviceInfo): Boolean {
        val knownFingerprints: List<String> = entityDeviceHandler.getEntityKnownFingerprints(entity.id)
        if(knownFingerprints.isNullOrEmpty()) {
            return false
        }

        if(deviceInfo.fingerprint.isNullOrBlank()) {
            return true
        }

        return !knownFingerprints.contains(deviceInfo.fingerprint)
    }
}