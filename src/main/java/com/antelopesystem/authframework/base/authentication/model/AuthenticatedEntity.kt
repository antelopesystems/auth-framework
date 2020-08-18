package com.antelopesystem.authframework.base.authentication.model

import com.antelopesystem.authframework.base.model.GrantRO
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import com.antelopesystem.crudframework.model.PersistentEntity
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "authenticated_entity")
class AuthenticatedEntity(
        var username: String = "",

        var password: String = "",

        var type: String = "",

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, mappedBy = "authenticatedEntity", orphanRemoval = true, cascade = [CascadeType.ALL])
        @JvmTransient
        var grants: MutableList<AuthenticatedEntityGrant> = mutableListOf<AuthenticatedEntityGrant>()
) : JpaBaseUpdatebleEntity()


@Entity
@Table(name = "authenticated_entity_grant")
class AuthenticatedEntityGrant(
        @get:Id
        @get:Column(name = "name", nullable = false)
        @MappedField(target = GrantRO::class)
        var name: String,

        @get:Column(name = "authenticated_entity_id", nullable = false)
        @get:Id
        var authenticatedEntityId: Long
) : PersistentEntity {
        @get:ManyToOne
        @get:JoinColumn(name = "authenticated_entity_id", nullable = false, updatable = false, insertable = false)
        @JvmTransient
        lateinit var authenticatedEntity: AuthenticatedEntity
}

typealias JvmTransient = kotlin.jvm.Transient