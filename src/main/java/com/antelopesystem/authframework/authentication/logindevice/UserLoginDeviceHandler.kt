package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.model.UserLoginDeviceRel
import java.util.*

interface UserLoginDeviceHandler {

    fun invalidateUserDevicesAfterDate(userId: Long, date: Date)

    fun getUserKnownIps(userId: Long): List<String>

    fun getUserKnownCountryIsos(userId: Long): List<String>

    fun getUserKnownUserAgents(userId: Long): List<String>

    fun getUserKnownFingerprints(userId: Long): List<String>

    fun createOrUpdateDevice(device: UserLoginDeviceRel): UserLoginDeviceRel
}