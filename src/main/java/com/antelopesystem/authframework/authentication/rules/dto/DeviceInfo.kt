package com.antelopesystem.authframework.authentication.rules.dto

data class DeviceInfo(
        val userAgent: String?,
        val ip: String?,
        val countryIso: String,
        val fingerprint: String?
)