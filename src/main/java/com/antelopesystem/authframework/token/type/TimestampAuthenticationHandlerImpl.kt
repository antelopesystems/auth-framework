package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.token.model.AuthToken
import com.antelopesystem.authframework.token.model.request.TimestampTokenRequest
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class TimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.Timestamp

    override fun getStringToHash(authToken: AuthToken, timestamp: String, request: HttpServletRequest) = (authToken.token + timestamp)

    override fun <T : TokenRequest> generateToken(authToken: AuthToken, payload: T): String {
        return encrypt(payload as TimestampTokenRequest, authToken.token)
    }


}