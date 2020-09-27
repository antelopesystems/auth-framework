package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.token.model.request.FingerprintedTimestampTokenRequest
import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
open class FingerprintedTimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.FingerprintedTimestamp

    override fun getStringToHash(authToken: AuthToken, timestamp: String, request: HttpServletRequest) = (authToken.token + timestamp + authToken.fingerprint)

    override fun <T : TokenRequest> generateToken(authToken: AuthToken, payload: T): String {
        payload as FingerprintedTimestampTokenRequest

        authToken.fingerprint = payload.fingerprint

        return encrypt(payload, authToken.token)
    }
}
