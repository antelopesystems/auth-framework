package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaType
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatableEntity
import javax.persistence.*
import javax.persistence.Entity as JpaEntity

@JpaEntity
@Table
class EntityMfaMethod(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "entity_id")
        var entity: Entity,
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
) : BaseJpaUpdatableEntity() {
        constructor(entity: Entity, type: MfaType, params: CustomParamsDTO): this(entity, type, params.param1, params.param2, params.param3, params.param4, params.param5)
}