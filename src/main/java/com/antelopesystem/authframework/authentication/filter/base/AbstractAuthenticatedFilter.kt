package com.antelopesystem.authframework.authentication.filter.base

import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.exception.InvalidTokenException
import com.antelopesystem.authframework.token.model.ObjectToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


abstract class AbstractAuthenticatedFilter(protected val objectType: String, private val tokenHandler: TokenHandler) : AbstractExceptionHandlingFilter() {

    @Autowired
    private lateinit var mappings: RequestMappingHandlerMapping

    public override fun doFilterInner(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        var requestWrapper = request
        if(request !is RequestWrapper) {
            requestWrapper = RequestWrapper(request)
        }
        if (tokenHandler.isTokenPresent(requestWrapper)) {
            val token = tokenHandler.getTokenFromRequest(requestWrapper)
            if (token.objectType != objectType) {
                throw InvalidTokenException()
            }
            processFilter(token, requestWrapper, response)
        }
        chain.doFilter(requestWrapper, response)
    }

    override fun getAlreadyFilteredAttributeName(): String = this.javaClass.simpleName

    protected abstract fun processFilter(token: ObjectToken, request: HttpServletRequest, response: HttpServletResponse)

    protected fun getRequestHandler(request: HttpServletRequest): HandlerMethod? {
        val handlerExecutionChain = mappings.getHandler(request)
        if (handlerExecutionChain != null) {
            return handlerExecutionChain.handler as HandlerMethod
        }
        return null
    }

}