package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface AuthenticationTypeHandler  {
    @get:ComponentMapKey
    val type: AuthenticationType

    fun isSupportedForType(type: String): Boolean = true

    fun isPasswordBased(): Boolean

    fun initializeLogin(payload: AuthenticationPayload, method: AuthenticatedEntityAuthenticationMethod) {
        throw UnsupportedOperationException("initializeLogin is not supported for [ $type ]")
    }

    fun doLogin(payload: AuthenticationPayload, method: AuthenticatedEntityAuthenticationMethod)

    fun initializeRegistration(payload: AuthenticationPayload) {
        throw UnsupportedOperationException("initializeRegistration is not supported for [ $type ]")
    }

    fun doRegister(payload: AuthenticationPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod

    fun getEntityAuthenticationType(payload: AuthenticationPayload): AuthenticatedEntityAuthenticationMethod?
}