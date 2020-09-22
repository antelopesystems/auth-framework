package com.antelopesystem.authframework.authentication.loginrules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.UserLoginDeviceHandler
import com.antelopesystem.authframework.authentication.loginrules.UserLoginAnomalyType
import com.antelopesystem.authframework.authentication.loginrules.anomaly.base.UserLoginAnomalyHandler
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownIpAnomalyHandlerImpl : UserLoginAnomalyHandler {

    @Autowired
    private lateinit var userLoginDeviceHandler: UserLoginDeviceHandler

    override val anomalyType: UserLoginAnomalyType
        get() = UserLoginAnomalyType.UNKNOWN_IP

    override fun handle(loginDTO: UserLoginDTO): Boolean {
        val knownIps: List<String> = userLoginDeviceHandler.getUserKnownIps(loginDTO.userId)
        if(knownIps.isNullOrEmpty()) {
            return false
        }

        if(loginDTO.ip.isBlank()) {
            return true
        }

        return !knownIps.contains(loginDTO.ip)
    }
}