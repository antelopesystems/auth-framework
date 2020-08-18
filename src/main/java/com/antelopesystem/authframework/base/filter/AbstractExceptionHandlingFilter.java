package com.antelopesystem.authframework.base.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractExceptionHandlingFilter extends OncePerRequestFilter {

	private static Logger logger = LoggerFactory.getLogger(AbstractExceptionHandlingFilter.class);

	@Override
	protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			doFilterInner(request, response, chain);
		} catch(RequestFailedException e) {
			response.getWriter().write(e.getMessage());
			response.setStatus(e.getStatusCode());
		} catch(Throwable e) {
			response.getWriter().write("Unknown error");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected String getAlreadyFilteredAttributeName() {
		return this.getClass().getSimpleName();
	}

	protected abstract void doFilterInner(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;


}
