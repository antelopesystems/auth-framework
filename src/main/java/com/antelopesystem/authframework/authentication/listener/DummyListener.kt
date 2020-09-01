package com.antelopesystem.authframework.authentication.listener

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import org.springframework.stereotype.Component

@Component
class DummyListener : RegistrationListener, LoginListener {

    override val type: String
        get() = "User"

    override fun onLoginSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        println("Dummy onLoginSuccess")
    }

    override fun onLoginFailure(payload: AuthenticationPayload, entity: AuthenticatedEntity, error: String) {
        println("Dummy onLoginFailure {$error}")
    }

    override fun onRegistrationSuccess(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        println("Dummy onRegistrationSuccess")
    }

    override fun onRegistrationFailure(payload: AuthenticationPayload, error: String) {
        println("Dummy onRegistrationError $error")
    }
}