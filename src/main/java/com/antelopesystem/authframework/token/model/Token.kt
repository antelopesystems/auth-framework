package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.authframework.util.cleanUuid
import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.BaseJpaEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "token")
@Deleteable(softDelete = true)
//@CachedBy()
data class Token(
        var token: String = UUID.randomUUID().cleanUuid(),

        @get:Enumerated
        var type: TokenType = TokenType.Legacy,

        var entityId: Long,

        var entityType: String,

        var ip: String?,

        var mfaRequired: Boolean = false,

        var passwordChangeRequired: Boolean = false,

        var sessionId: String = UUID.randomUUID().cleanUuid(),

        var fingerprint: String? = "",

        @get:Temporal(TemporalType.TIMESTAMP)
        var expiryTime: Date? = null,

        var score: Int = 0
): BaseJpaEntity()