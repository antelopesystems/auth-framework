package com.antelopesystem.authframework.authentication.loginrules.anomaly

import com.antelopesystem.authframework.authentication.logindevice.UserLoginDeviceHandler
import com.antelopesystem.authframework.authentication.loginrules.UserLoginAnomalyType
import com.antelopesystem.authframework.authentication.loginrules.anomaly.base.UserLoginAnomalyHandler
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnknownFingerprintAnomalyHandlerImpl : UserLoginAnomalyHandler {

    @Autowired
    private lateinit var userLoginDeviceHandler: UserLoginDeviceHandler

    override val anomalyType: UserLoginAnomalyType
        get() = UserLoginAnomalyType.UNKNOWN_FINGERPRINT

    override fun handle(loginDTO: UserLoginDTO): Boolean {
        val knownFingerprints: List<String> = userLoginDeviceHandler.getUserKnownFingerprints(loginDTO.userId)
        if(knownFingerprints.isNullOrEmpty()) {
            return false
        }

        if(loginDTO.fingerprint.isNullOrBlank()) {
            return true
        }

        return !knownFingerprints.contains(loginDTO.fingerprint)
    }
}