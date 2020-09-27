package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.device.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.device.model.EntityDevice
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import org.springframework.stereotype.Component

@Component
class DeviceListener(
        private val authenticationValidator: AuthenticationValidator,
        private val crudHandler: CrudHandler,
        private val entityDeviceHandler: EntityDeviceHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
) : LoginListener, RegistrationListener {

    override val type: String?
        get() = null

    override fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken, deviceInfo: DeviceInfo) {
        if(shouldValidateAuthentication(method.entity)) {
            authToken.score = authenticationValidator.validate(method.entity, deviceInfo)
            crudHandler.update(authToken).execute()
        }
        entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))

    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken, deviceInfo: DeviceInfo) {
        entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))
    }

    private fun shouldValidateAuthentication(entity: Entity): Boolean {
        return securitySettingsHandler.getSecuritySettings(entity.type).authenticationValidationEnabled
    }
}