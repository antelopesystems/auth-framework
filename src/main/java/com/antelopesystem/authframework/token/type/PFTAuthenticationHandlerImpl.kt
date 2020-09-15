package com.antelopesystem.authframework.token.type

import com.antelopesystem.authframework.authentication.filter.base.RequestWrapper
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.type.enums.TokenType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class PFTAuthenticationHandlerImpl: FingerprintedTimestampAuthenticationHandlerImpl() {

    override val type: TokenType = TokenType.PFT

    override fun getStringToHash(token: ObjectToken, timestamp: String, request: HttpServletRequest): String {
        request as RequestWrapper
        val requestUri = cleanPayload(request.requestURI?.substring(request.contextPath.length))
        val queryString = cleanPayload(request.queryString)
        val body = cleanPayload(request.body)

        val payload = super.getStringToHash(token, timestamp, request) + requestUri + queryString + body
        return payload
    }

    private fun cleanPayload(target: String?): String {
        return target?.replace(Regex("[^A-Za-z0-9]"), "") ?: ""
    }
}