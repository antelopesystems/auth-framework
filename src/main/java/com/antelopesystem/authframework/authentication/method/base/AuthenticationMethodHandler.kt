package com.antelopesystem.authframework.authentication.method.base

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface AuthenticationMethodHandler  {
    @get:ComponentMapKey
    val method: AuthenticationMethod

    fun isSupportedForType(type: String): Boolean = true

    fun isSupportedForPayload(payload: MethodRequestPayload): Boolean

    fun isPasswordBased(): Boolean

    fun initializeLogin(payload: MethodRequestPayload, method: AuthenticatedEntityAuthenticationMethod) {
        throw UnsupportedOperationException("initializeLogin is not supported for [ ${this.method} ]")
    }

    fun doLogin(payload: MethodRequestPayload, method: AuthenticatedEntityAuthenticationMethod)

    fun initializeRegistration(payload: MethodRequestPayload) {
        throw UnsupportedOperationException("initializeRegistration is not supported for [ $method ]")
    }

    fun doRegister(payload: MethodRequestPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod

    fun getEntityMethod(payload: MethodRequestPayload): AuthenticatedEntityAuthenticationMethod?
}