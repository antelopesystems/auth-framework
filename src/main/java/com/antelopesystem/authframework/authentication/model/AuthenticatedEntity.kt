package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.util.JvmTransient
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "authenticated_entity")
class AuthenticatedEntity(
        var type: String = "",

        @get:Column(name = "is_active")
        var active: Boolean = true,

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, mappedBy = "authenticatedEntity", orphanRemoval = true, cascade = [CascadeType.ALL])
        @JvmTransient
        var grants: MutableList<AuthenticatedEntityGrant> = mutableListOf<AuthenticatedEntityGrant>(),

        @get:Fetch(FetchMode.SELECT)
        @get:OneToMany(fetch = FetchType.EAGER, targetEntity = AuthenticatedEntityAuthenticationMethod::class, mappedBy = "entity", orphanRemoval = true, cascade = [CascadeType.ALL])
        var authenticationMethods: MutableList<AuthenticatedEntityAuthenticationMethod> = mutableListOf()
) : JpaBaseUpdatebleEntity()


