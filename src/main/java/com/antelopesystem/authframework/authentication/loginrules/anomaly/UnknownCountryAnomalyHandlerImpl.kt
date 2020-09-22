package com.antelopesystem.authframework.authentication.loginrules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.UserLoginDeviceHandler
import com.antelopesystem.authframework.authentication.loginrules.UserLoginAnomalyType
import com.antelopesystem.authframework.authentication.loginrules.anomaly.base.UserLoginAnomalyHandler
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownCountryAnomalyHandlerImpl : UserLoginAnomalyHandler {

    @Autowired
    private lateinit var userLoginDeviceHandler: UserLoginDeviceHandler

    override val anomalyType: UserLoginAnomalyType
        get() = UserLoginAnomalyType.UNKNOWN_COUNTRY

    override fun handle(loginDTO: UserLoginDTO): Boolean {
        val knownCountryIsos = userLoginDeviceHandler.getUserKnownCountryIsos(loginDTO.userId)
        if(knownCountryIsos.isNullOrEmpty()) {
            return false
        }

        if(loginDTO.countryIso.isBlank()) {
            return true
        }

        return !knownCountryIsos.contains(loginDTO.countryIso)
    }
}