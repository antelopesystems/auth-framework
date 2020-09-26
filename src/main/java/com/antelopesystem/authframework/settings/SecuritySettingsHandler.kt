package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettings
import com.antelopesystem.authframework.settings.model.Settings

interface SecuritySettingsHandler {
    fun getSecuritySettings(entityType: String): SecuritySettings
    fun getSettings(entityType: String): Settings
}