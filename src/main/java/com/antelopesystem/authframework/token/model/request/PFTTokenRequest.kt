package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.type.enums.TokenType

class PFTTokenRequest(objectId: Long, objectType: String, ip: String, publicKey: String, fingerprint: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false) : FingerprintedTimestampTokenRequest(objectId, objectType, ip, publicKey, fingerprint, passwordChangeRequired, totpApproved) {
    override val type = TokenType.PFT
}