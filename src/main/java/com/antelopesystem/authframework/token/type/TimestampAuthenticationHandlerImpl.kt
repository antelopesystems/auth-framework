package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.token.model.Token
import com.antelopesystem.authframework.token.model.request.TimestampTokenRequest
import com.antelopesystem.authframework.token.model.TokenRequest
import com.antelopesystem.authframework.token.type.base.AbstractTimestampAuthenticationHandler
import com.antelopesystem.authframework.token.type.enums.TokenType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class TimestampAuthenticationHandlerImpl: AbstractTimestampAuthenticationHandler() {

    override val type: TokenType = TokenType.Timestamp

    override fun getStringToHash(token: Token, timestamp: String, request: HttpServletRequest) = (token.token + timestamp)

    override fun <T : TokenRequest> generateToken(token: Token, payload: T): String {
        return encrypt(payload as TimestampTokenRequest, token.token)
    }


}