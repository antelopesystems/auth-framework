package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Service

class AuthenticationServiceImpl : AuthenticationService {

    @ComponentMap(key = AuthenticationType::class, value = AuthenticationTypeHandler::class)
    private lateinit var authenticationTypeHandlers: Map<AuthenticationType, AuthenticationTypeHandler>

    override fun initializeLogin(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
        val entity = handler.getEntity(payload) ?: error("Entity not found")
        handler.initializeLogin(payload, entity)
    }

    override fun doLogin(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
        val entity = handler.getEntity(payload) ?: error("Entity not found")
        handler.doLogin(payload, entity)
    }

    override fun initializeRegistration(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
        handler.initializeRegistration(payload)
    }

    override fun doRegister(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
        handler.doRegister(payload, AuthenticatedEntity())
    }

    private fun getTypeHandler(authenticationType: AuthenticationType): AuthenticationTypeHandler {
        return authenticationTypeHandlers[authenticationType] ?: error("Handler for authentication type [ $authenticationType ] not found")
    }
}