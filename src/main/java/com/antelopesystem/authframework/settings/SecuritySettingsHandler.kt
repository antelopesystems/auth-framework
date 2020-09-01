package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettings

interface SecuritySettingsHandler {
    fun getSecuritySettings(objectType: String): SecuritySettings
}