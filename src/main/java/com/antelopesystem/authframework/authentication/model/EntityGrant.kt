package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.JvmTransient
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.model.PersistentEntity
import java.io.Serializable
import javax.persistence.*
import javax.persistence.Entity as JpaEntity

@JpaEntity
@Table
class EntityGrant(
        @get:Id
        @get:Column(name = "name", nullable = false)
        @MappedField(target = GrantRO::class)
        var name: String,

        @get:Column(name = "entity_id", nullable = false)
        @get:Id
        var entityId: Long
) : PersistentEntity, Serializable {
        @get:ManyToOne
        @get:JoinColumn(name = "entity_id", nullable = false, updatable = false, insertable = false)
        @JvmTransient
        lateinit var entity: Entity
}