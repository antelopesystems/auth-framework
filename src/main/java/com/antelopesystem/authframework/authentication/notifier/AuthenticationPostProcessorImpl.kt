package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.DeviceInfoProvider
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.ForgotPasswordListener
import com.antelopesystem.authframework.authentication.notifier.postaction.ExternalEntityCreator
import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.beans.ImmutableBean
import org.springframework.stereotype.Component


// todo: modify component map to support lists
@Component
class AuthenticationPostProcessorImpl(
        @Autowired(required=false) private val loginListeners: List<LoginListener> = listOf(),
        @Autowired(required=false) private val registrationListeners: List<RegistrationListener> = listOf(),
        @Autowired(required=false) private val forgotPasswordListeners: List<ForgotPasswordListener> = listOf(),
        private val crudHandler: CrudHandler,
        private val deviceInfoProvider: DeviceInfoProvider
) : AuthenticationPostProcessor {
    @ComponentMap
    private lateinit var externalEntityCreator: Map<String, ExternalEntityCreator>

    override fun onLoginSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken) {
        getLoginListenersForEntity(method.entity.type).forEach {
            it.onLoginSuccess(payload, method, authToken, deviceInfoProvider.getDeviceInfoFromCurrentRequest())
        }
    }

    override fun onLoginFailure(payload: MethodRequestPayload, method: EntityAuthenticationMethod, error: String) {
        getLoginListenersForEntity(method.entity.type).forEach {
            it.onLoginFailure(payload, method, deviceInfoProvider.getDeviceInfoFromCurrentRequest(), error)
        }
    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, method: EntityAuthenticationMethod, authToken: AuthToken) {
        resolveAndLinkExternalId(method.entity)
        getRegistrationListenersForEntity(method.entity.type).forEach {
            it.onRegistrationSuccess(payload, method, authToken, deviceInfoProvider.getDeviceInfoFromCurrentRequest())
        }

    }

    override fun onRegistrationFailure(payload: MethodRequestPayload, error: String) {
        getRegistrationListenersForEntity(payload.type).forEach {
            it.onRegistrationFailure(payload, deviceInfoProvider.getDeviceInfoFromCurrentRequest(), error)
        }
    }

    override fun onForgotPasswordInitialized(token: String, method: EntityAuthenticationMethod) {
        getForgotPasswordListenersForEntity(method.entity.type).forEach {
            it.onForgotPasswordInitialized(token, method, deviceInfoProvider.getDeviceInfoFromCurrentRequest())
        }
    }

    override fun onForgotPasswordSuccess(method: EntityAuthenticationMethod) {
        getForgotPasswordListenersForEntity(method.entity.type).forEach {
            it.onForgotPasswordSuccess(method, deviceInfoProvider.getDeviceInfoFromCurrentRequest())
        }
    }

    private fun getForgotPasswordListenersForEntity(type: String): List<ForgotPasswordListener> {
        return forgotPasswordListeners.filter { it.type == type || it.type == null }
    }

    private fun getLoginListenersForEntity(type: String): List<LoginListener> {
        return loginListeners.filter { it.type == type || it.type == null }
    }

    private fun getRegistrationListenersForEntity(type: String): List<RegistrationListener> {
        return registrationListeners.filter { it.type == type || it.type == null }
    }

    private fun resolveAndLinkExternalId(entity: Entity) {
        val externalEntityCreator = externalEntityCreator[entity.type] ?: return
        val externalId = externalEntityCreator.create(ImmutableBean.create(entity) as Entity)
        entity.externalId = externalId
        crudHandler.update(entity).execute()
    }

}