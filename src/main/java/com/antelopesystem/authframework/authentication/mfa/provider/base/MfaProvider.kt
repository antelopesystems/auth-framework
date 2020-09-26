package com.antelopesystem.authframework.authentication.mfa.provider.base

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

enum class MfaType {
    Nexmo, Authenticator
}

interface MfaProvider {

    @get:ComponentMapKey
    val type: MfaType

    fun setup(payload: MethodRequestPayload, entity: Entity): CustomParamsDTO

    fun issue(entity: Entity, params: CustomParamsDTO) {
        throw UnsupportedOperationException("issue is not supported for [ $type ]")
    }

    fun validate(code: String, entity: Entity, params: CustomParamsDTO) {
        throw UnsupportedOperationException("validate is not supported for [ $type ]")
    }
}