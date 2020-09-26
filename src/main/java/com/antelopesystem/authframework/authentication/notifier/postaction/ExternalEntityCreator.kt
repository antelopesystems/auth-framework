package com.antelopesystem.authframework.authentication.notifier.postaction

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface ExternalEntityCreator {
    @get:ComponentMapKey
    val type: String

    /**
     * Called when an authenticated entity is created, used to create a matching external entity
     * @param The authenticated entity which was created
     * @return The ID of the external entity
     */
    fun create(entity: Entity): String
}