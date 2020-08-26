package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.JvmTransient
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.model.PersistentEntity
import java.io.Serializable
import javax.persistence.*

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
) : PersistentEntity, Serializable {
        @get:ManyToOne
        @get:JoinColumn(name = "authenticated_entity_id", nullable = false, updatable = false, insertable = false)
        @JvmTransient
        lateinit var authenticatedEntity: AuthenticatedEntity
}