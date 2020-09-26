package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.type.enums.TokenType

open class FingerprintedTimestampTokenRequest(entityId: Long, entityType: String, ip: String, publicKey: String, val fingerprint: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : TimestampTokenRequest(entityId, entityType, ip, publicKey, passwordChangeRequired, mfaRequired) {
    override val type = TokenType.FingerprintedTimestamp
}