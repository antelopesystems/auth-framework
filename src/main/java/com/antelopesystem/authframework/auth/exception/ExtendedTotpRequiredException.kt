package com.antelopesystem.authframework.auth.exception

data class ExtendedTotpRequiredException(val uuid: String? = null) : RuntimeException("Extended OTP validation required")