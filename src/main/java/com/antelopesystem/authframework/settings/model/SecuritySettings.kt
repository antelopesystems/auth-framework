package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaType
import com.antelopesystem.authframework.token.type.enums.TokenType
import java.io.Serializable

class SecuritySettings(
        var tokenLifetimeHours: Long = 24L,

        var authenticator: AuthenticatorSettings = AuthenticatorSettings(),

        var nexmo: NexmoSettings = NexmoSettings(),

        var google: GoogleSettings = GoogleSettings(),

        var allowedTokenTypes: MutableSet<TokenType> = TokenType.values().toMutableSet(),

        var defaultAuthenticationMethod: AuthenticationMethod? = null,

        var allowedAuthenticationMethods: MutableSet<AuthenticationMethod> = mutableSetOf(),

        var allowedMfaTypes: MutableSet<MfaType> = mutableSetOf(),

        var authenticationValidationEnabled: Boolean = false,

        var passwordRegex: String = ".*",

        var passwordExpiryDays: Long = 0,

        var allowRegistrationOnLogin: Boolean = false,

        var allowLoginOnRegistration: Boolean = false
) : Serializable

data class AuthenticatorSettings(
        var issuer: String = "Not configured"
) : Serializable

data class NexmoSettings(
        var apiKey: String = "",
        var apiSecret: String = "",
        var brand: String = ""
) : Serializable

data class GoogleSettings(
        var clientId: String = "",
        var clientSecret: String = ""
) : Serializable