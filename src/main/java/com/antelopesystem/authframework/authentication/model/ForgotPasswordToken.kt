package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.cleanUuid
import com.antelopesystem.crudframework.crud.annotation.DeleteColumn
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatableEntity
import java.util.*
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Entity as JpaEntity

@JpaEntity
@Table
@Deleteable(softDelete = true)
data class ForgotPasswordToken(
        @get:ManyToOne(fetch = FetchType.EAGER)
        @get:JoinColumn(name = "method_id")
        var method: EntityAuthenticationMethod,

        var entityId: Long,

        var token: String = UUID.randomUUID().cleanUuid(),

        @DeleteColumn
        var expired: Boolean = false,

        var deviceHash: String = ""
) : BaseJpaUpdatableEntity()