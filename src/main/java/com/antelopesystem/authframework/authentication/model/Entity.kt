package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.JvmTransient
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedFields
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatableEntity
import com.antelopesystem.crudframework.transformer.EntityListToRoListTransformer
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*
import javax.persistence.Entity

@Entity
@Table
@MappedFields(
        MappedField(target = UserInfo::class, mapFrom = "id", mapTo = "entityId")
)
class Entity(
        @MappedField(target = UserInfo::class, mapTo = "entityType")
        var type: String = "",

        @get:Column(name = "is_active")
        var active: Boolean = true,

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, mappedBy = "entity", orphanRemoval = true, cascade = [CascadeType.ALL])
        @JvmTransient
        @MappedField(target = UserInfo::class, transformer = EntityListToRoListTransformer::class)
        var grants: MutableList<EntityGrant> = mutableListOf(),

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, targetEntity = EntityAuthenticationMethod::class, mappedBy = "entity", orphanRemoval = true, cascade = [CascadeType.ALL])
        var authenticationMethods: MutableList<EntityAuthenticationMethod> = mutableListOf(),

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, targetEntity = EntityMfaMethod::class, mappedBy = "entity", orphanRemoval = true, cascade = [CascadeType.ALL])
        var mfaMethods: MutableList<EntityMfaMethod> = mutableListOf(),

        @MappedField(target = UserInfo::class)
        var externalId: String? = null,

        var email: String? = null,

        var telephone: String? = null
) : BaseJpaUpdatableEntity()