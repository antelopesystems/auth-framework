package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettingsRO

interface SecuritySettingsHandler {
    fun getSecuritySettings(objectType: String): SecuritySettingsRO
}