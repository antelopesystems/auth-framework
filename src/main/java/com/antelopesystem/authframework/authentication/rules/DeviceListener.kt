package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.logindevice.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.logindevice.model.EntityDevice
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.model.ObjectToken
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

    override fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, deviceInfo: DeviceInfo) {
        if(shouldValidateAuthentication(entity)) {
            token.score = authenticationValidator.validate(entity, deviceInfo)
            crudHandler.update(token).execute()
        }
        entityDeviceHandler.createOrUpdateDevice(EntityDevice(entity.id, deviceInfo))

    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, deviceInfo: DeviceInfo) {
        entityDeviceHandler.createOrUpdateDevice(EntityDevice(entity.id, deviceInfo))
    }

    private fun shouldValidateAuthentication(entity: AuthenticatedEntity): Boolean {
        return securitySettingsHandler.getSecuritySettings(entity.type).authenticationValidationEnabled
    }
}