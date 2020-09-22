package com.antelopesystem.authframework.authentication.loginrules

import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import org.springframework.stereotype.Component

@Component
class LoginDeviceListener : LoginListener {

    override val type: String?
        get() = null

    override fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity, loginDto: UserLoginDTO) {
//        super.onLoginSuccess(payload, entity)
    }
}