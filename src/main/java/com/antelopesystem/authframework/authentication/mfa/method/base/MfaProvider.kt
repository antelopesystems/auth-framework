package com.antelopesystem.authframework.authentication.mfa.method.base

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

enum class MfaType {
    Nexmo, Authenticator
}

interface MfaProvider {

    @get:ComponentMapKey
    val type: MfaType

    fun isSupportedForType(entityType: String): Boolean = true

    fun setup(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityMfaMethod

    fun issue(method: EntityMfaMethod) {
        throw UnsupportedOperationException("issue is not supported for [ $type ]")
    }

    fun validate(code: String, method: EntityMfaMethod) {
        throw UnsupportedOperationException("validate is not supported for [ $type ]")
    }
}