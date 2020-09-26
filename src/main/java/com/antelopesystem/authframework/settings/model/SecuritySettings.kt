package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "security_settings")
class SecuritySettings(
        var entityType: String,

        var tokenLifetimeHours: Long = 24L,

        var authenticatorMfaEnabled: Boolean = false,

        var authenticatorName: String? = "Not configured", // change to issuer todo

        var nexmoMfaEnabled: Boolean = false,

        var nexmoAuthenticationEnabled: Boolean = false,

        var nexmoApiKey: String = "",

        var nexmoApiSecret: String = "",

        var nexmoBranding: String = "",

        var allowedTokenTypes: String = TokenType.values().joinToString(","),

        @get:Enumerated(EnumType.STRING)
        var defaultAuthenticationMethod: AuthenticationMethod? = null,

        @get:Fetch(FetchMode.SELECT)
        @get:ElementCollection(fetch = FetchType.EAGER)
        @get:CollectionTable(name = "settings_authentication_method_rel", joinColumns = [JoinColumn(name = "settings_id")])
        @get:Column(name = "method")
        @get:Enumerated(EnumType.STRING)
        var allowedAuthenticationMethods: MutableList<AuthenticationMethod> = mutableListOf(),

        var allowedMfaTypes: String = "",

        var authenticationValidationEnabled: Boolean = false,

        var passwordRegex: String = ".*",

        var passwordExpiryDays: Long = 0,

        var allowRegistrationOnLogin: Boolean = false,

        var allowLoginOnRegistration: Boolean = false
) : BaseJpaUpdatebleEntity() {
        @Transient
        fun getAllowedTokenTypeEnums() : List<TokenType> {
                return allowedTokenTypes.split(",")
                        .map { TokenType.valueOf(it) }
        }
}