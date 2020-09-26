package com.antelopesystem.authframework.authentication.method.base

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface AuthenticationMethodHandler  {
    @get:ComponentMapKey
    val method: AuthenticationMethod

    fun isSupportedForPayload(payload: MethodRequestPayload): Boolean

    fun getUsernameFromPayload(payload: MethodRequestPayload) : String

    fun isPasswordBased(): Boolean

    fun initializeLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod): CustomParamsDTO = CustomParamsDTO()

    fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod)

    fun initializeRegistration(payload: MethodRequestPayload): CustomParamsDTO

    fun doRegister(payload: MethodRequestPayload, params: CustomParamsDTO, entity: Entity): EntityAuthenticationMethod

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

    fun getUsername(method: EntityAuthenticationMethod): String
}