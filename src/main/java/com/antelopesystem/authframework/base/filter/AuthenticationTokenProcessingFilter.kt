package com.antelopesystem.authframework.base.filter;

import com.antelopesystem.authframework.token.model.ObjectToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that validate presence of authentication token
 * in request to REST services.
 */
public class AuthenticationTokenProcessingFilter extends BaseAuthenticatedFilter {

	//------------------------ Fields --------------------------

	private final AuthenticationManager authenticationManager;

	//------------------------ Public methods ------------------
	//------------------------ Constructors --------------------
	//------------------------ Field's handlers ----------------
	//------------------------ Other public methods ------------


	public AuthenticationTokenProcessingFilter(String objectType, AuthenticationManager authenticationManager) {
		super(objectType);
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void processFilter(ObjectToken token, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// determine the operator based on the (already validated) token
		// build an Authentication object with the operator's info
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(token.getObjectId(), "");
		authentication.setDetails(
				new WebAuthenticationDetailsSource().buildDetails(request));
		// set the authentication into the SecurityContext
		SecurityContextHolder.getContext().setAuthentication(
				authenticationManager.authenticate(authentication));
	}


	//------------------------ Private methods -----------------
}

