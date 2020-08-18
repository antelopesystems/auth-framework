package com.antelopesystem.authframework.auth.model

import com.antelopesystem.authframework.auth.type.enums.TokenType

data class TokenResponse(val token: String, val sessionId: String, val tokenType: TokenType)
