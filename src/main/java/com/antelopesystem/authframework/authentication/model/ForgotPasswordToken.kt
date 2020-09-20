package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.cleanUuid
import com.antelopesystem.crudframework.crud.annotation.DeleteColumn
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import java.util.*
import javax.persistence.*

@Entity
@Table
@Deleteable(softDelete = true)
data class ForgotPasswordToken(
    @get:ManyToOne(fetch = FetchType.EAGER)
    @get:JoinColumn(name = "entity_method_id")
    var entityMethod: EntityAuthenticationMethod,

    var token: String = UUID.randomUUID().cleanUuid(),

    @DeleteColumn
    var expired: Boolean = false
) : BaseJpaUpdatebleEntity()