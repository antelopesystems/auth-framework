package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface AuthenticationTypeHandler {
    @get:ComponentMapKey
    val type: AuthenticationType

    fun initializeLogin(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        throw UnsupportedOperationException("initializeLogin is not supported for [ $type ]")
    }

    fun doLogin(payload: AuthenticationPayload, entity: AuthenticatedEntity)

    fun initializeRegistration(payload: AuthenticationPayload) {
        throw UnsupportedOperationException("initializeRegistration is not supported for [ $type ]")
    }

    fun doRegister(payload: AuthenticationPayload, authenticatedEntity: AuthenticatedEntity): AuthenticatedEntity
    fun getEntity(payload: AuthenticationPayload): AuthenticatedEntity?
}