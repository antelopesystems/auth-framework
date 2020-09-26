package com.antelopesystem.authframework.authentication.device

import com.antelopesystem.authframework.authentication.device.model.EntityDevice
import java.util.*

interface EntityDeviceHandler {

    fun invalidateEntityDevicesAfterDate(entityId: Long, date: Date)

    fun getEntityKnownIps(entityId: Long): List<String>

    fun getEntityKnownCountryIsos(entityId: Long): List<String>

    fun getEntityKnownUserAgents(entityId: Long): List<String>

    fun getEntityKnownFingerprints(entityId: Long): List<String>

    fun createOrUpdateDevice(device: EntityDevice): EntityDevice
}