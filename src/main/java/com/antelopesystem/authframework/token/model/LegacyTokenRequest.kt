package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

class LegacyTokenRequest(objectId: Long, objectType: String, ip: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false, originalObjectId: Long = 0L) : TokenRequest(objectId, objectType, ip, passwordChangeRequired, totpApproved, originalObjectId) {

    override val type = TokenType.Legacy
}