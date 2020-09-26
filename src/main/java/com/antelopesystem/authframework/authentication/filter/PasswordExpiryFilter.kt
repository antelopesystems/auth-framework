package com.antelopesystem.authframework.authentication.filter

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.annotations.BypassPasswordExpiredCheck
import com.antelopesystem.authframework.authentication.filter.base.AbstractAuthenticatedFilter
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.Token
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PasswordExpiryFilter
(
        tokenHandler: TokenHandler
) : AbstractAuthenticatedFilter(tokenHandler) {
    @Throws(IOException::class, ServletException::class)
    override fun processFilter(token: Token, request: HttpServletRequest, response: HttpServletResponse) {
        val handler = getRequestHandler(request) ?: return
        if(token.passwordChangeRequired) {
            handler.getMethodAnnotation(BypassPasswordExpiredCheck::class.java) ?: throw AccessDeniedException("Password expired")
        }
    }
}