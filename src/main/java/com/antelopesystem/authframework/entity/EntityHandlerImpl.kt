package com.antelopesystem.authframework.entity

import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Component

@Component
class EntityHandlerImpl(
        private val crudHandler: CrudHandler
) : EntityHandler {
    @ComponentMap
    private lateinit var authenticationMethodHandlers: Map<AuthenticationMethod, AuthenticationMethodHandler>

    override fun getEntity(entityId: Long, entityType: String): Entity {
        return crudHandler.showBy(where {
            "id" Equal entityId
            "type" Equal entityType
        }, Entity::class.java).execute() ?: error("No entity found")
    }

    override fun getEntityUsername(entityId: Long, entityType: String): String {
        return getEntityUsername(getEntity(entityId, entityType))
    }

    override fun getEntityUsername(entity: Entity): String {
        val primaryMethod = entity.authenticationMethods.find { it.primary } ?: return "Unknown"
        val handler = authenticationMethodHandlers[primaryMethod.method] ?: return "Unknown"
        return handler.getUsername(primaryMethod)
    }
}