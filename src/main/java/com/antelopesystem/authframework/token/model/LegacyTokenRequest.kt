package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

class LegacyTokenRequest(objectId: Long, objectType: String, ip: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false) : TokenRequest(objectId, objectType, ip, passwordChangeRequired, totpApproved) {

    override val type = TokenType.Legacy
}