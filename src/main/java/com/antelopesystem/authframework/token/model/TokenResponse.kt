package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

data class TokenResponse(val token: String, val sessionId: String, val tokenType: TokenType)
