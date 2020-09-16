package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType

class LegacyTokenRequest(objectId: Long, objectType: String, ip: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : TokenRequest(objectId, objectType, ip, passwordChangeRequired, mfaRequired) {

    override val type = TokenType.Legacy
}