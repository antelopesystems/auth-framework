package com.antelopesystem.authframework.token.model.request

import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.enums.TokenType

open class TimestampTokenRequest(objectId: Long, objectType: String, ip: String, val publicKey: String, passwordChangeRequired: Boolean = false, mfaRequired: Boolean = false) : TokenRequest(objectId, objectType, ip, passwordChangeRequired, mfaRequired) {

    override val type = TokenType.Timestamp

}