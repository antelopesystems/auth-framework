package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.model.UserLoginDeviceRel
import com.antelopesystem.crudframework.jpa.dao.BaseDao
import java.util.*

interface UserLoginDeviceDao : BaseDao {

    fun getUserKnownIps(userId: Long): List<String>

    fun getUserKnownCountryIsos(userId: Long): List<String>

    fun getUserKnownUserAgents(userId: Long): List<String>

    fun getUserKnownFingerprints(userId: Long): List<String>

    fun getValidDevicesAfterDate(userId: Long, date: Date): List<UserLoginDeviceRel>
}