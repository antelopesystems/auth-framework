package com.antelopesystem.authframework.authentication.method.base

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface AuthenticationMethodHandler  {
    @get:ComponentMapKey
    val method: AuthenticationMethod

    fun isSupportedForType(type: String): Boolean = true

    fun isSupportedForPayload(payload: MethodRequestPayload): Boolean

    fun getUsernameFromPayload(payload: MethodRequestPayload) : String

    fun isPasswordBased(): Boolean

    fun initializeLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod): Any? {
        throw UnsupportedOperationException("initializeLogin is not supported for [ ${this.method} ]")
    }

    fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod)

    fun initializeRegistration(payload: MethodRequestPayload): Any? {
        throw UnsupportedOperationException("initializeRegistration is not supported for [ $method ]")
    }

    fun doRegister(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityAuthenticationMethod

    fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod?

    fun changePassword(newPassword: String, method: EntityAuthenticationMethod) {
        throw UnsupportedOperationException("changePassword is not supported for [ ${this.method} ]")
    }

    fun checkPassword(payload: MethodRequestPayload, method: EntityAuthenticationMethod): Boolean {
        throw UnsupportedOperationException("checkPassword is not supported for [ ${this.method} ]")
    }

    fun isPasswordExpired(method: EntityAuthenticationMethod): Boolean {
        throw UnsupportedOperationException("isPasswordExpired is not supported for [ ${this.method} ]")
    }
}