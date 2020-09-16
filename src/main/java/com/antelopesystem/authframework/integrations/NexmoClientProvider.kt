package com.antelopesystem.authframework.integrations

import com.antelopesystem.authframework.settings.SecuritySettingsHandler

class NexmoClientProvider(
        private val securitySettingsHandler: SecuritySettingsHandler
) {
    private val nexmoClients = mutableMapOf<String, NexmoClient>()

    fun getNexmoClient(objectType: String) : NexmoClient {
        return nexmoClients.getOrPut(objectType) {
            val settings = securitySettingsHandler.getSecuritySettings(objectType)
            NexmoClient(settings.nexmoApiKey, settings.nexmoApiSecret, settings.nexmoBranding)
        }
    }
}