package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.fieldmapper.transformer.annotation.EnumType
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@Table(name = "security_settings")
class SecuritySettings(
        @get:Column
        var objectType: String,

        @get:Column
        var tokenLifetimeHours: Long = 24L,

        @get:Column
        var authenticatorMfaEnabled: Boolean = false,

        @get:Column
        var authenticatorName: String? = "Not configured",

        @get:Column
        var nexmoMfaEnabled: Boolean = false,

        @get:Column
        var nexmoAuthenticationEnabled: Boolean = false,

        @get:Column
        var nexmoApiKey: String = "",

        @get:Column
        var nexmoApiSecret: String = "",

        @get:Column
        var nexmoBranding: String = "",

        @get:Column
        var allowedTokenTypes: String = TokenType.values().joinToString(","),

        @get:Column
        var suspiciousLoginCheckEnabled: Boolean = false,

        @get:Column
        var passwordRegex: String = ".*",

        var passwordExpiryDays: Long = 0,

        @get:Column
        var allowRegistrationOnLogin: Boolean = false,

        @get:Column
        var allowLoginOnRegistration: Boolean = false
) : JpaBaseUpdatebleEntity() {
        @Transient
        fun getAllowedTokenTypeEnums() : List<TokenType> {
                return allowedTokenTypes.split(",")
                        .map { TokenType.valueOf(it) }
        }
}