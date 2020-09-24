package com.antelopesystem.authframework.authentication.loginrules

import com.antelopesystem.authframework.authentication.logindevice.UserLoginDeviceHandler
import com.antelopesystem.authframework.authentication.logindevice.model.UserLoginDeviceRel
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import org.springframework.stereotype.Component

@Component
class DeviceListener(
        private val loginValidator: UserLoginValidator,
        private val crudHandler: CrudHandler,
        private val userLoginDeviceHandler: UserLoginDeviceHandler
) : LoginListener, RegistrationListener {

    override val type: String?
        get() = null

    override fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, loginDto: UserLoginDTO) {
        token.score = loginValidator.validateLogin(loginDto)
        crudHandler.update(token).execute()
        userLoginDeviceHandler.createOrUpdateDevice(UserLoginDeviceRel(loginDto))

    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, token: ObjectToken, loginDto: UserLoginDTO) {
        userLoginDeviceHandler.createOrUpdateDevice(UserLoginDeviceRel(loginDto))
    }
}