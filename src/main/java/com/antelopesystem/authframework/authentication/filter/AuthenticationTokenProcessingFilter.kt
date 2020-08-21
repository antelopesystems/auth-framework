package com.antelopesystem.authframework.authentication.filter

import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.model.ObjectToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationTokenProcessingFilter
(
        objectType: String,
        tokenHandler: TokenHandler,
        private val authenticationManager: AuthenticationManager
) : BaseAuthenticatedFilter(objectType, tokenHandler) {
    @Throws(IOException::class, ServletException::class)
    override fun processFilter(token: ObjectToken?, request: HttpServletRequest?, response: HttpServletResponse?) {
        val authentication = UsernamePasswordAuthenticationToken(token!!.objectId, "")
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(authentication)
    }
}