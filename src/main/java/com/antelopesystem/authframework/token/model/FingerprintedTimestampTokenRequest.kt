package com.antelopesystem.authframework.token.model

import com.antelopesystem.authframework.token.type.enums.TokenType

class FingerprintedTimestampTokenRequest(objectId: Long, objectType: String, ip: String, publicKey: String, val fingerprint: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false) : TimestampTokenRequest(objectId, objectType, ip, publicKey, passwordChangeRequired, totpApproved) {
    override val type = TokenType.FingerprintedTimestamp
}