package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.crudframework.crud.annotation.DeleteColumn
import com.antelopesystem.crudframework.crud.annotation.Deleteable
import com.antelopesystem.crudframework.jpa.model.JpaBaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "object_token")
@Deleteable(softDelete = true)
@CachedBy("com.mycompany.cache.TOKEN_CACHE")
data class ObjectToken(

        @get:Column(name = "token")
        var token: String = UUID.randomUUID().toString(),

        @get:Enumerated
        @get:Column(name = "token_type", columnDefinition = "INT(11) DEFAULT 0")
        var tokenType: TokenType = TokenType.Legacy,

        @get:Column(name = "object_id")
        var objectId: Long,

        @get:Column(name = "object_type")
        var objectType: String,

        @get:Column(name = "original_object_id", nullable = false, columnDefinition = "BIGINT(11) DEFAULT 0")
        var originalObjectId: Long,

        @get:Column(name = "ip")
        var ip: String?,

        @get:Column(name = "totp_approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        var totpApproved: Boolean = false,

        @get:Column(name = "password_change_required", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        var passwordChangeRequired: Boolean = false,

        @get:Column(name = "immutable", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        var immutable: Boolean = false,

        @get:Column(name = "session_id")
        var sessionId: String = UUID.randomUUID().toString(),

        @get:Column(name = "fingerprint")
        var fingerprint: String? = "",

        @get:Temporal(TemporalType.TIMESTAMP)
        @get:Column(name = "expiry_time")
        var expiryTime: Date? = null
): JpaBaseEntity()