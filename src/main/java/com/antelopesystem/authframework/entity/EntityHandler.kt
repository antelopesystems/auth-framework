package com.antelopesystem.authframework.entity

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity

interface EntityHandler {
    fun getEntity(entityId: Long, entityType: String): AuthenticatedEntity
    fun getEntityUsername(entityId: Long, entityType: String): String
    fun getEntityUsername(entity: AuthenticatedEntity): String
}