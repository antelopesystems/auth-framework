package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.fieldmapper.annotation.DefaultMappingTarget
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.fieldmapper.transformer.CommaDelimitedStringToEnumListTransformer
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "security_settings")
@DefaultMappingTarget(SecuritySettingsRO::class)
class SecuritySettings(
        @MappedField
        @get:Column
        var objectType: String,

        @MappedField
        @get:Column
        var tokenLifetimeHours: Long = 24L,

        @MappedField
        @get:Column
        var otpRequired: Boolean = false,

        @MappedField
        @get:Column
        var authenticatorOtpEnabled: Boolean = false,

        @MappedField
        @get:Column
        var authenticatorOtpName: String? = "Not configured",

        @MappedField
        @get:Column
        var nexmoOtpEnabled: Boolean = false,

        @MappedField
        @get:Column
        var nexmoAuthenticationEnabled: Boolean = false,

        @MappedField
        @get:Column
        var nexmoApiKey: String = "",

        @MappedField
        @get:Column
        var nexmoApiSecret: String = "",

        @MappedField
        @get:Column
        var nexmoBranding: String = "",

        @MappedField(transformer = CommaDelimitedStringToEnumListTransformer::class)
        @get:Column
        var allowedTokenTypes: String = TokenType.values().joinToString(", "),

        @MappedField
        @get:Column
        var suspiciousLoginCheckEnabled: Boolean = false,

        @MappedField
        @get:Column
        var passwordRegex: String = ".*",

        var allowRegisterOnLogin: Boolean = false,

        var allowLoginOnRegistration: Boolean = false
) : JpaBaseUpdatebleEntity()