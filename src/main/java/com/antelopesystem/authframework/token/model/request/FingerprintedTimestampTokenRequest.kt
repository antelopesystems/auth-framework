package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.model.request.TimestampTokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType

open class FingerprintedTimestampTokenRequest(objectId: Long, objectType: String, ip: String, publicKey: String, val fingerprint: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false) : TimestampTokenRequest(objectId, objectType, ip, publicKey, passwordChangeRequired, totpApproved) {
    override val type = TokenType.FingerprintedTimestamp
}