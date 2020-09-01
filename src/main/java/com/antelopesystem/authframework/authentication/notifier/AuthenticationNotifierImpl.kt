package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import org.springframework.beans.factory.annotation.Autowired


// todo: modify component map to support lists
class AuthenticationNotifierImpl(
        @Autowired(required=false) private val loginListeners: List<LoginListener> = listOf(),
        @Autowired(required=false) private val registrationListeners: List<RegistrationListener> = listOf()
) : AuthenticationNotifier {

    override fun onLoginSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity) {
        getLoginListenersForEntity(entity.type).forEach {
            it.onLoginSuccess(payload, entity)
        }
    }

    override fun onLoginFailure(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity, error: String) {
        getLoginListenersForEntity(entity.type).forEach {
            it.onLoginFailure(payload, entity, error)
        }
    }

    override fun onRegistrationSuccess(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity) {
        getRegistrationListenersForEntity(entity.type).forEach {
            it.onRegistrationSuccess(payload, entity)
        }
    }

    override fun onRegistrationFailure(payload: AuthenticationRequestPayload, error: String) {
        getRegistrationListenersForEntity(payload.type).forEach {
            it.onRegistrationFailure(payload, error)
        }
    }

    private fun getLoginListenersForEntity(type: String): List<LoginListener> {
        return loginListeners.filter { it.type == type }
    }

    private fun getRegistrationListenersForEntity(type: String): List<RegistrationListener> {
        return registrationListeners.filter { it.type == type }
    }
}