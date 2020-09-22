package com.antelopesystem.authframework.authentication.loginrules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.UserLoginDeviceHandler
import com.antelopesystem.authframework.authentication.loginrules.UserLoginAnomalyType
import com.antelopesystem.authframework.authentication.loginrules.anomaly.base.UserLoginAnomalyHandler
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownUserAgentAnomalyHandlerImpl : UserLoginAnomalyHandler {

    @Autowired
    private lateinit var userLoginDeviceHandler: UserLoginDeviceHandler

    override val anomalyType: UserLoginAnomalyType
        get() = UserLoginAnomalyType.UNKNOWN_USER_AGENT

    override fun handle(loginDTO: UserLoginDTO): Boolean {
        val knownUserAgents: List<String> = userLoginDeviceHandler.getUserKnownUserAgents(loginDTO.userId)
        if(knownUserAgents.isNullOrEmpty()) {
            return false
        }

        if(loginDTO.userAgent.isBlank()) {
            return true
        }

        return !knownUserAgents.contains(loginDTO.userAgent)
    }
}