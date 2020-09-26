package com.antelopesystem.authframework.entity

import com.antelopesystem.authframework.authentication.model.Entity

interface EntityHandler {
    fun getEntity(entityId: Long, entityType: String): Entity
    fun getEntityUsername(entityId: Long, entityType: String): String
    fun getEntityUsername(entity: Entity): String
}