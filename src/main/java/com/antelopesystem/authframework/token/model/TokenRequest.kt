package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

abstract class TokenRequest(val objectId: Long, val objectType: String, val ip: String, val passwordChangeRequired: Boolean, val mfaRequired: Boolean) {

    abstract val type: TokenType
}