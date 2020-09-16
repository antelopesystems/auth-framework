package com.antelopesystem.authframework.authentication.filter

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.annotations.BypassMfa
import com.antelopesystem.authframework.authentication.annotations.BypassPasswordExpiredCheck
import com.antelopesystem.authframework.authentication.filter.base.AbstractAuthenticatedFilter
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.ObjectToken
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MfaFilter
(
        objectType: String,
        tokenHandler: TokenHandler
) : AbstractAuthenticatedFilter(objectType, tokenHandler) {
    @Throws(IOException::class, ServletException::class)
    override fun processFilter(token: ObjectToken, request: HttpServletRequest, response: HttpServletResponse) {
        val handler = getRequestHandler(request) ?: return
        if(token.mfaRequired) {
            handler.getMethodAnnotation(BypassMfa::class.java) ?: throw AccessDeniedException("Mfa required")
        }
    }
}