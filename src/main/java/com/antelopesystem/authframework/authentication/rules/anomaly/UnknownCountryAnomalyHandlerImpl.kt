package com.antelopesystem.authframework.authentication.rules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.rules.DeviceAnomalyType
import com.antelopesystem.authframework.authentication.rules.anomaly.base.DeviceAnomalyHandler
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownCountryAnomalyHandlerImpl : DeviceAnomalyHandler {

    @Autowired
    private lateinit var entityDeviceHandler: EntityDeviceHandler

    override val anomalyType: DeviceAnomalyType
        get() = DeviceAnomalyType.UNKNOWN_COUNTRY

    override fun handle(entity: AuthenticatedEntity, deviceInfo: DeviceInfo): Boolean {
        val knownCountryIsos = entityDeviceHandler.getEntityKnownCountryIsos(entity.id)
        if(knownCountryIsos.isNullOrEmpty()) {
            return false
        }

        if(deviceInfo.countryIso.isNullOrBlank()) {
            return true
        }

        return !knownCountryIsos.contains(deviceInfo.countryIso)
    }
}