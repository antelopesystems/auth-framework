package com.antelopesystem.authframework.auth.type

import com.antelopesystem.authframework.auth.model.FingerprintedTimestampTokenRequest
import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.authframework.auth.model.TokenRequest
import com.antelopesystem.authframework.auth.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.auth.type.enums.TokenType
import org.springframework.stereotype.Component

@Component
class FingerprintedTimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.FingerprintedTimestamp

    override fun getStringToHash(token: ObjectToken, timestamp: String) = (token.token + timestamp + token.fingerprint)

    override fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String {
        payload as FingerprintedTimestampTokenRequest

        objectToken.fingerprint = payload.fingerprint

        return encrypt(payload, objectToken.token)
    }
}
