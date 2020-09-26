package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.type.enums.TokenType

class PFTTokenRequest(entityId: Long, entityType: String, ip: String, publicKey: String, fingerprint: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : FingerprintedTimestampTokenRequest(entityId, entityType, ip, publicKey, fingerprint, passwordChangeRequired, mfaRequired) {
    override val type = TokenType.PFT
}