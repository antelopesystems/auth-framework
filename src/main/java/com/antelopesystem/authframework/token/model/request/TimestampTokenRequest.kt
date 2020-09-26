package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType

open class TimestampTokenRequest(entityId: Long, entityType: String, ip: String, val publicKey: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : TokenRequest(entityId, entityType, ip, passwordChangeRequired, mfaRequired) {

    override val type = TokenType.Timestamp

}