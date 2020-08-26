package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.fieldmapper.transformer.annotation.EnumType
import com.antelopesystem.crudframework.jpa.ro.BaseUpdatableJpaRO

class SecuritySettingsRO(
        var objectType: String = "",
        var tokenLifetimeHours: Long = 24L,

        var otpRequired: Boolean = false,

        var authenticatorOtpEnabled: Boolean = false,
        var authenticatorOtpName: String = "Not configured",

        var nexmoOtpEnabled: Boolean = false,
        var nexmoAuthenticationEnabled: Boolean = false,
        var nexmoApiKey: String = "",
        var nexmoApiSecret: String = "",
        var nexmoBranding: String = "",

        @EnumType(TokenType::class)
        var allowedTokenTypes: List<TokenType> = listOf(),

        var suspiciousLoginCheckEnabled: Boolean = false,

        var passwordRegex: String = ".*"

) : BaseUpdatableJpaRO()