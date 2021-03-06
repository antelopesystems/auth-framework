package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.authframework.authentication.device.EntityDeviceHandler
import com.antelopesystem.authframework.authentication.device.model.EntityDevice
import com.antelopesystem.authframework.authentication.log.ActionLog
import com.antelopesystem.authframework.authentication.log.AuthenticationLogAction
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.ForgotPasswordToken
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.ForgotPasswordListener
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
) : LoginListener, RegistrationListener, ForgotPasswordListener {

    override val type: String?
        get() = null

    override fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken, deviceInfo: DeviceInfo) {
        val device = entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))
        authToken.deviceHash = device.hash
        if(shouldValidateAuthentication(method.entity)) {
            authToken.score = authenticationValidator.validate(method.entity, deviceInfo)
        }
        crudHandler.update(authToken).execute()

        crudHandler.create(ActionLog(
                method.entity.id,
                method.id,
                device.hash,
                AuthenticationLogAction.Login,
                true
        ))
                .execute()
    }

    override fun onLoginFailure(payload: MethodRequestPayload, method: EntityAuthenticationMethod, deviceInfo: DeviceInfo, error: String) {
        val device = entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))

        crudHandler.create(ActionLog(
                method.entity.id,
                method.id,
                device.hash,
                AuthenticationLogAction.Registration,
                false,
                error
        ))
                .execute()
    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken, deviceInfo: DeviceInfo) {
        val device = entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))

        crudHandler.create(ActionLog(
                method.entity.id,
                method.id,
                device.hash,
                AuthenticationLogAction.Registration,
                true
        ))
                .execute()
    }

    override fun onForgotPasswordInitialized(token: String, method: EntityAuthenticationMethod, deviceInfo: DeviceInfo) {
        val device = entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))

        crudHandler.create(ActionLog(
                method.entity.id,
                method.id,
                device.hash,
                AuthenticationLogAction.InitializeForgotPassword,
                true
        ))
                .execute()
    }

    override fun onForgotPasswordSuccess(token: ForgotPasswordToken, method: EntityAuthenticationMethod, deviceInfo: DeviceInfo) {
        val device = entityDeviceHandler.createOrUpdateDevice(EntityDevice(method.entity.id, deviceInfo))
        crudHandler.create(ActionLog(
                method.entity.id,
                method.id,
                device.hash,
                AuthenticationLogAction.CompleteForgotPassword,
                true
        ))
                .execute()
        token.deviceHash = device.hash
        crudHandler.update(token).execute()
    }

    private fun shouldValidateAuthentication(entity: Entity): Boolean {
        return securitySettingsHandler.getSecuritySettings(entity.type).authenticationValidationEnabled
    }
}