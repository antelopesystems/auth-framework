package com.antelopesystem.authframework.authentication.rules

import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.Table

@Entity
@Table(name = "authentication_rule")
@Deleteable(softDelete = false)
//@CachedBy()
data class AuthenticationRule(
        @get:Column(name = "min_score", columnDefinition = "INT DEFAULT 0")
        var minScore: Int = 0,

        @get:Enumerated
        @get:Column(name = "action")
        var action: RuleActionType? = null
) : BaseJpaUpdatebleEntity()