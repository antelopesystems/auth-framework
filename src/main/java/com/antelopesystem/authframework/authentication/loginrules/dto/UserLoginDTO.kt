package com.antelopesystem.authframework.authentication.loginrules.dto

data class UserLoginDTO(
        val userAgent: String?,
        val ip: String?,
        val countryIso: String,
        val fingerprint: String?,
        val userId: Long
)