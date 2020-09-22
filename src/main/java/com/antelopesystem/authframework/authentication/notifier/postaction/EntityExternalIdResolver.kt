package com.antelopesystem.authframework.authentication.notifier.postaction

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface EntityExternalIdResolver {
    @get:ComponentMapKey
    val type: String

    fun resolve(entity: AuthenticatedEntity): String
}