package com.antelopesystem.authframework.integrations.nexmo

import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class NexmoClientProvider(
        private val securitySettingsHandler: SecuritySettingsHandler
) {
    private val nexmoClients = mutableMapOf<String, NexmoClient>()

    fun getNexmoClient(entityType: String) : NexmoClient {
        return nexmoClients.getOrPut(entityType) {
            val settings = securitySettingsHandler.getSecuritySettings(entityType)
            NexmoClient(settings.nexmo.apiKey, settings.nexmo.apiSecret, settings.nexmo.brand)
        }
    }
}