package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.util.getFingerprint
import com.antelopesystem.authframework.util.getIpAddress
import com.antelopesystem.authframework.util.getUserAgent
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class DeviceInfoProvider(
        private val request: HttpServletRequest
) {

    fun getDeviceInfoFromCurrentRequest(): DeviceInfo {
        return getDeviceInfo(request)
    }

    fun getDeviceInfo(request: HttpServletRequest): DeviceInfo {
        return DeviceInfo(
                request.getUserAgent(),
                request.getIpAddress(),
                "XX", // todo
                request.getFingerprint()
        )
    }
}