package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.crudframework.fieldmapper.annotation.DefaultMappingTarget
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedFields
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.*

@Entity
@Table
@DefaultMappingTarget(EntityMfaMethod::class)
class EntityMfaMethod(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "entity_id")
        @MappedFields(
                MappedField(mapFrom = "entity.id", mapTo = "entityId"),
                MappedField(mapFrom = "entity.type", mapTo = "entityType")
        )
        var entity: AuthenticatedEntity,
        @MappedField
        var type: MfaType,
        @MappedField
        var param1: String = "",
        @MappedField
        var param2: String = "",
        @MappedField
        var param3: String = "",
        @MappedField
        var param4: String = "",
        @MappedField
        var param5: String = ""
) : BaseJpaUpdatebleEntity()