package com.antelopesystem.authframework.base.filter

import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.exception.InvalidTokenException
import com.antelopesystem.authframework.token.model.ObjectToken
import org.springframework.beans.factory.annotation.Autowired
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


abstract class BaseAuthenticatedFilter(protected val objectType: String, private val tokenHandler: TokenHandler) : AbstractExceptionHandlingFilter() {
    public override fun doFilterInner(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (tokenHandler.isTokenPresent(request)) {
            val token = tokenHandler.getTokenFromRequest(request)
            if (token.objectType != objectType) {
                throw InvalidTokenException()
            }
            processFilter(token, request, response)
        }
        chain.doFilter(request, response)
    }

    override fun getAlreadyFilteredAttributeName(): String = this.javaClass.simpleName

    protected abstract fun processFilter(token: ObjectToken?, request: HttpServletRequest?, response: HttpServletResponse?)

}