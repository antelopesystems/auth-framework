package com.antelopesystem.authframework.authentication.notifier

import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.notifier.listener.ForgotPasswordListener
import com.antelopesystem.authframework.authentication.notifier.postaction.EntityExternalIdResolver
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
        private val crudHandler: CrudHandler
) : AuthenticationPostProcessor {
    @ComponentMap
    private lateinit var externalIdResolvers: Map<String, EntityExternalIdResolver>

    override fun onLoginSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity) {
        getLoginListenersForEntity(entity.type).forEach {
            it.onLoginSuccess(payload, entity)
        }
    }

    override fun onLoginFailure(payload: MethodRequestPayload, entity: AuthenticatedEntity, error: String) {
        getLoginListenersForEntity(entity.type).forEach {
            it.onLoginFailure(payload, entity, error)
        }
    }

    override fun onRegistrationSuccess(payload: MethodRequestPayload, entity: AuthenticatedEntity) {
        getRegistrationListenersForEntity(entity.type).forEach {
            it.onRegistrationSuccess(payload, entity)
        }
        resolveAndLinkExternalId(entity)
    }

    override fun onRegistrationFailure(payload: MethodRequestPayload, error: String) {
        getRegistrationListenersForEntity(payload.type).forEach {
            it.onRegistrationFailure(payload, error)
        }
    }

    override fun onForgotPasswordInitialized(token: String, entity: AuthenticatedEntity) {
        getForgotPasswordListenersForEntity(entity.type).forEach {
            it.onForgotPasswordInitialized(token, entity)
        }
    }

    override fun onForgotPasswordSuccess(entity: AuthenticatedEntity) {
        getForgotPasswordListenersForEntity(entity.type).forEach {
            it.onForgotPasswordSuccess(entity)
        }
    }

    private fun getForgotPasswordListenersForEntity(type: String): List<ForgotPasswordListener> {
        return forgotPasswordListeners.filter { it.type == type }
    }

    private fun getLoginListenersForEntity(type: String): List<LoginListener> {
        return loginListeners.filter { it.type == type }
    }

    private fun getRegistrationListenersForEntity(type: String): List<RegistrationListener> {
        return registrationListeners.filter { it.type == type }
    }

    private fun resolveAndLinkExternalId(entity: AuthenticatedEntity) {
        val externalIdResolver = externalIdResolvers[entity.type] ?: return
        val externalId = externalIdResolver.resolve(ImmutableBean.create(entity) as AuthenticatedEntity)
        entity.externalId = externalId
        crudHandler.update(entity).execute()
    }

}