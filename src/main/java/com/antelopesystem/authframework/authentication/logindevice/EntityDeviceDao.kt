package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.model.EntityDevice
import com.antelopesystem.crudframework.jpa.dao.BaseDao
import java.util.*

interface EntityDeviceDao : BaseDao {

    fun getEntityKnownIps(entityId: Long): List<String>

    fun getEntityKnownCountryIsos(entityId: Long): List<String>

    fun getEntityKnownUserAgents(entityId: Long): List<String>

    fun getEntityKnownFingerprints(entityId: Long): List<String>

    fun getValidDevicesAfterDate(entityId: Long, date: Date): List<EntityDevice>
}