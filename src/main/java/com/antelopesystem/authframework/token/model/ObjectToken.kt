package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.BaseJpaEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "object_token")
@Deleteable(softDelete = true)
@CachedBy("com.mycompany.cache.TOKEN_CACHE")
data class ObjectToken(
        var token: String = UUID.randomUUID().toString(),

        @get:Enumerated
        var tokenType: TokenType = TokenType.Legacy,

        var objectId: Long,

        var objectType: String,

        var ip: String?,

        var mfaRequired: Boolean = false,

        var passwordChangeRequired: Boolean = false,

        var immutable: Boolean = false,

        var sessionId: String = UUID.randomUUID().toString(),

        var fingerprint: String? = "",

        @get:Temporal(TemporalType.TIMESTAMP)
        var expiryTime: Date? = null
): BaseJpaEntity()