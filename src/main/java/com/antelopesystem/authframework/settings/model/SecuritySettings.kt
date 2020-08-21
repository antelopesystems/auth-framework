package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.fieldmapper.annotation.DefaultMappingTarget
import com.antelopesystem.crudframework.fieldmapper.annotation.MappedField
import com.antelopesystem.crudframework.fieldmapper.transformer.CommaDelimitedStringToEnumListTransformer
import com.antelopesystem.crudframework.jpa.model.JpaBaseUpdatebleEntity
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "security_settings")
@DefaultMappingTarget(SecuritySettingsRO::class)
class SecuritySettings(
        @MappedField
        var objectType: String,

        @MappedField
        var tokenLifetimeHours: Long = 24L,

        @MappedField
        var otpRequired: Boolean = false,

        @MappedField
        var authenticatorOtpEnabled: Boolean = false,

        @MappedField
        var authenticatorOtpName: String = "Not configured",

        @MappedField
        var smsOtpEnabled: Boolean = false,

        @MappedField
        var smsAuthenticationEnabled: Boolean = false,

        @MappedField
        var smsApiKey: String = "",

        @MappedField(transformer = CommaDelimitedStringToEnumListTransformer::class)
        var allowedTokenTypes: String = TokenType.values().joinToString(", "),

        @MappedField
        var suspiciousLoginCheckEnabled: Boolean = false,

        @MappedField
        var passwordRegex: String = ".*"
) : JpaBaseUpdatebleEntity()