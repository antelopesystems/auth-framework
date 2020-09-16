package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import javax.persistence.*

@Entity
@Table
class EntityMfaMethod(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "entity_id")
        var entity: AuthenticatedEntity,
        var type: MfaType,
        var param1: String = "",
        var param2: String = "",
        var param3: String = "",
        var param4: String = "",
        var param5: String = ""
) : JpaBaseUpdatebleEntity()