package com.antelopesystem.authframework.integrations.nexmo

data class NumberRequestDTO(val requestId: String, val timeOfRequest: Long = System.currentTimeMillis())