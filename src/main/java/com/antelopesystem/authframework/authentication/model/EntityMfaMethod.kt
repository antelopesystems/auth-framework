package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.crudframework.fieldmapper.annotation.DefaultMappingTarget
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedFields
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.*

@Entity
@Table
class EntityMfaMethod(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "entity_id")
        var entity: AuthenticatedEntity,
        @MappedField(target = CustomParamsDTO::class)
        var type: MfaType,
        @MappedField(target = CustomParamsDTO::class)
        var param1: String = "",
        @MappedField(target = CustomParamsDTO::class)
        var param2: String = "",
        @MappedField(target = CustomParamsDTO::class)
        var param3: String = "",
        @MappedField(target = CustomParamsDTO::class)
        var param4: String = "",
        @MappedField(target = CustomParamsDTO::class)
        var param5: String = ""
) : BaseJpaUpdatebleEntity() {
        constructor(entity: AuthenticatedEntity, type: MfaType, params: CustomParamsDTO): this(entity, type, params.param1, params.param2, params.param3, params.param4, params.param5)
}