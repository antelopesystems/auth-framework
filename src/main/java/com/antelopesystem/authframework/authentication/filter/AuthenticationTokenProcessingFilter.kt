package com.antelopesystem.authframework.authentication.filter

import com.antelopesystem.authframework.authentication.filter.base.AbstractAuthenticatedFilter
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.ObjectToken
import com.antelopesystem.authframework.token.model.TokenAuthenticationRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationTokenProcessingFilter
(
        tokenHandler: TokenHandler,
        private val authenticationManager: AuthenticationManager
) : AbstractAuthenticatedFilter(tokenHandler) {
    @Throws(IOException::class, ServletException::class)
    override fun processFilter(token: ObjectToken, request: HttpServletRequest, response: HttpServletResponse) {
        val authentication = TokenAuthenticationRequest(token)
//        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(authentication)
    }
}