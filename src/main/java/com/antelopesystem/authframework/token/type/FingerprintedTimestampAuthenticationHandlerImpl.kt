package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.token.model.request.FingerprintedTimestampTokenRequest
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
open class FingerprintedTimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.FingerprintedTimestamp

    override fun getStringToHash(token: ObjectToken, timestamp: String, request: HttpServletRequest) = (token.token + timestamp + token.fingerprint)

    override fun <T : TokenRequest> generateToken(objectToken: ObjectToken, payload: T): String {
        payload as FingerprintedTimestampTokenRequest

        objectToken.fingerprint = payload.fingerprint

        return encrypt(payload, objectToken.token)
    }
}
