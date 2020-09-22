package com.antelopesystem.authframework.integrations

import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class AuthenticatorClientProvider(
        private val securitySettingsHandler: SecuritySettingsHandler
) {
    private val authenticatorClients = mutableMapOf<String, AuthenticatorClient>()

    fun getAuthenticatorClient(objectType: String) : AuthenticatorClient {
        return authenticatorClients.getOrPut(objectType) {
            val settings = securitySettingsHandler.getSecuritySettings(objectType)
            AuthenticatorClient(settings.authenticatorName ?: "Not configured")
        }
    }
}