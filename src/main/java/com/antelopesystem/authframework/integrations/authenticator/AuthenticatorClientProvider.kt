package com.antelopesystem.authframework.integrations.authenticator

import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class AuthenticatorClientProvider(
        private val securitySettingsHandler: SecuritySettingsHandler
) {
    private val authenticatorClients = mutableMapOf<String, AuthenticatorClient>()

    fun getAuthenticatorClient(entityType: String) : AuthenticatorClient {
        return authenticatorClients.getOrPut(entityType) {
            val settings = securitySettingsHandler.getSecuritySettings(entityType)
            AuthenticatorClient(settings.authenticatorName ?: "Not configured")
        }
    }
}