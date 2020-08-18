package com.antelopesystem.authframework.auth.model

import com.antelopesystem.authframework.auth.type.enums.TokenType

open class TimestampTokenRequest(objectId: Long, objectType: String, ip: String, val publicKey: String, passwordChangeRequired: Boolean = false, totpApproved: Boolean = false, originalObjectId: Long = 0L) : TokenRequest(objectId, objectType, ip, passwordChangeRequired, totpApproved, originalObjectId) {

    override val type = TokenType.Timestamp

}