package com.antelopesystem.authframework.auth.type

import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.authframework.auth.model.TimestampTokenRequest
import com.antelopesystem.authframework.auth.model.TokenRequest
import com.antelopesystem.authframework.auth.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.auth.type.enums.TokenType
import org.springframework.stereotype.Component


@Component
class TimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.Timestamp

    override fun getStringToHash(token: ObjectToken, timestamp: String) = (token.token + timestamp)

    override fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String {
        return encrypt(payload as TimestampTokenRequest, objectToken.token)
    }
}