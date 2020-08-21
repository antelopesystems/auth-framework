package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

class FingerprintedTimestampTokenRequest(objectId: Long, objectType: String, ip: String, publicKey: String, val fingerprint: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false, originalObjectId: Long = 0L) : TimestampTokenRequest(objectId, objectType, ip, publicKey, passwordChangeRequired, totpApproved, originalObjectId) {
    override val type = TokenType.FingerprintedTimestamp
}