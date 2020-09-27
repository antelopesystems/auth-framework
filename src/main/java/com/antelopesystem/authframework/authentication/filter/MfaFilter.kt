package com.antelopesystem.authframework.authentication.filter

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.annotations.BypassMfa
import com.antelopesystem.authframework.authentication.filter.base.AbstractAuthenticatedFilter
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.AuthToken
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MfaFilter
(
        tokenHandler: TokenHandler
) : AbstractAuthenticatedFilter(tokenHandler) {
    @Throws(IOException::class, ServletException::class)
    override fun processFilter(authToken: AuthToken, request: HttpServletRequest, response: HttpServletResponse) {
        val handler = getRequestHandler(request) ?: return
        if(authToken.mfaRequired) {
            handler.getMethodAnnotation(BypassMfa::class.java) ?: throw AccessDeniedException("Mfa required")
        }
    }
}