package com.antelopesystem.authframework.base.filter;

import com.antelopesystem.authframework.auth.OperatorAuthenticationHandler;
import com.antelopesystem.authframework.auth.exception.InvalidTokenException;
import com.antelopesystem.authframework.auth.model.ObjectToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseAuthenticatedFilter extends AbstractExceptionHandlingFilter {
	protected final String objectType;

	protected static Log securityLog = LogFactory.getLog("securityfilter");

	@Autowired
	protected OperatorAuthenticationHandler operatorAuthenticationHandler;

	public BaseAuthenticatedFilter(String objectType) {
		this.objectType = objectType;
	}

	@Override
	public final void doFilterInner(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(operatorAuthenticationHandler.isTokenPresent((request))) {
			ObjectToken token = operatorAuthenticationHandler.getTokenFromRequest(request);
			if(!token.getObjectType().equals(objectType)) {
				throw new InvalidTokenException();
			}

			processFilter(token, request, response);
		}
		chain.doFilter(request, response);
	}

	@Override
	protected String getAlreadyFilteredAttributeName() {
		return this.getClass().getSimpleName();
	}

	protected abstract void processFilter(ObjectToken token, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
