package com.antelopesystem.authframework.base.filter

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
abstract class AbstractExceptionHandlingFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            doFilterInner(request, response, chain)
        } catch (e: RequestFailedException) {
            response.writer.write(e.message)
            response.status = e.statusCode
        } catch (e: Throwable) {
            response.writer.write("Unknown error")
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            logger.error(e.message, e)
        }
    }

    override fun getAlreadyFilteredAttributeName(): String {
        return this.javaClass.simpleName
    }

    @Throws(IOException::class, ServletException::class)
    protected abstract fun doFilterInner(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain)

    companion object {
        private val logger = LoggerFactory.getLogger(AbstractExceptionHandlingFilter::class.java)
    }
}