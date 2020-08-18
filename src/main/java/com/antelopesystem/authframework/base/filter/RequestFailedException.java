package com.antelopesystem.authframework.base.filter;

import org.springframework.http.HttpStatus;

public class RequestFailedException extends RuntimeException {

	private final int statusCode;

	public RequestFailedException(int statusCode) {
		super("Request Failed");
		this.statusCode = statusCode;
	}

	public RequestFailedException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public RequestFailedException() {
		this(HttpStatus.FORBIDDEN.value());
	}

	public RequestFailedException(String message) {
		this(message, HttpStatus.FORBIDDEN.value());
	}

	public int getStatusCode() {
		return statusCode;
	}
}
