package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.type.enums.TokenType

open class FingerprintedTimestampTokenRequest(objectId: Long, objectType: String, ip: String, publicKey: String, val fingerprint: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : TimestampTokenRequest(objectId, objectType, ip, publicKey, passwordChangeRequired, mfaRequired) {
    override val type = TokenType.FingerprintedTimestamp
}