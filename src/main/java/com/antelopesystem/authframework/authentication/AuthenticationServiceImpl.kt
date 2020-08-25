package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl() : AuthenticationService {

    @ComponentMap(key = AuthenticationType::class, value = AuthenticationTypeHandler::class)
    private lateinit var authenticationTypeHandlers: Map<AuthenticationType, AuthenticationTypeHandler>

    override fun initializeLogin(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
    }

    override fun doLogin(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
    }

    override fun initializeRegistration(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
    }

    override fun doRegistration(payload: AuthenticationPayload) {
        val handler = getTypeHandler(payload.authenticationType)
    }

    private fun getTypeHandler(authenticationType: AuthenticationType): AuthenticationTypeHandler {
        return authenticationTypeHandlers[authenticationType] ?: error("Handler for authentication type [ $authenticationType ] not found")
    }
}