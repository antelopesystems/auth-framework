package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.settings.model.SecuritySettingsRO

abstract class GenericPayloadWrapper(val payload: AuthenticationPayload) {
}

abstract class AbstractAuthenticationTypeHandler(
        protected val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationTypeHandler {
    protected fun getSecuritySettings(objectType: String): SecuritySettingsRO {
        return securitySettingsHandler.getSecuritySettings(objectType)
    }
}