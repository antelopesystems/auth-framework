package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.JvmTransient
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "authenticated_entity")
class AuthenticatedEntity(
        var username: String = "",

        var password: String = "",

        var type: String = "",

        var telephonePrefix: String = "",

        var telephone: String = "",

        @get:Column(name = "is_active")
        var active: Boolean = true,

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, mappedBy = "authenticatedEntity", orphanRemoval = true, cascade = [CascadeType.ALL])
        @JvmTransient
        var grants: MutableList<AuthenticatedEntityGrant> = mutableListOf<AuthenticatedEntityGrant>()
) : JpaBaseUpdatebleEntity() {
        @get:Transient
        val fullTelephone: String get() = telephonePrefix + telephone
}


