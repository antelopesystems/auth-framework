package com.antelopesystem.authframework.base.filter;

import com.antelopesystem.authframework.token.TokenHandler;
import com.antelopesystem.authframework.token.exception.InvalidTokenException;
import com.antelopesystem.authframework.token.model.ObjectToken;
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

	@Autowired
	protected TokenHandler tokenHandler;

	public BaseAuthenticatedFilter(String objectType) {
		this.objectType = objectType;
	}

	@Override
	public final void doFilterInner(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(tokenHandler.isTokenPresent((request))) {
			ObjectToken token = tokenHandler.getTokenFromRequest(request);
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
