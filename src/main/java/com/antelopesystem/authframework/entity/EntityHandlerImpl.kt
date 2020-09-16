package com.antelopesystem.authframework.entity

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.stereotype.Component

@Component
class EntityHandlerImpl(
        private val crudHandler: CrudHandler
) : EntityHandler {
    override fun getEntity(entityId: Long, entityType: String): AuthenticatedEntity {
        return crudHandler.showBy(where {
            "id" Equal entityId
            "type" Equal entityType
        }, AuthenticatedEntity::class.java).execute() ?: error("No entity found")
    }
}