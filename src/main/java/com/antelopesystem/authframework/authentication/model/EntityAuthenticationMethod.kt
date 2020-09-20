package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.*

@Entity
@Table
class EntityAuthenticationMethod(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "entity_id")
        var entity: AuthenticatedEntity,
        var method: AuthenticationMethod,
        var param1: String = "",
        var param2: String = "",
        var param3: String = "",
        var param4: String = "",
        var param5: String = "",

        @get:Column(name = "is_active")
        var active: Boolean = true
) : BaseJpaUpdatebleEntity()