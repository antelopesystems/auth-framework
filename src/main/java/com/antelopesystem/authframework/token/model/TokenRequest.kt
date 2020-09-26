package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

abstract class TokenRequest(val entityId: Long, val entityType: String, val ip: String, val passwordChangeRequired: Boolean, val mfaRequired: Boolean) {

    abstract val type: TokenType
}