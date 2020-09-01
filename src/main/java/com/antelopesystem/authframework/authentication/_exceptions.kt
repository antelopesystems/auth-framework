package com.antelopesystem.authframework.authentication

import org.springframework.http.HttpStatus

abstract class RequestFailedException : RuntimeException {
    val statusCode: Int

    @JvmOverloads
    constructor(statusCode: Int = HttpStatus.FORBIDDEN.value()) : super("Request Failed") {
        this.statusCode = statusCode
    }

    @JvmOverloads
    constructor(message: String?, statusCode: Int = HttpStatus.FORBIDDEN.value()) : super(message) {
        this.statusCode = statusCode
    }
}

open class AuthenticationMethodException(message: String) : RuntimeException(message) {
    constructor(cause: Throwable) : this(cause.message.toString()) {
        initCause(cause)
    }
}

class LoginFailedException(message: String) : AuthenticationMethodException(message) {
    constructor(cause: Throwable) : this(cause.message.toString()) {
        initCause(cause)
    }
}

class RegistrationFailedException(message: String) : AuthenticationMethodException(message) {
    constructor(cause: Throwable) : this(cause.message.toString()) {
        initCause(cause)
    }
}

open class AccessDeniedException(message: String) : RequestFailedException("Access Denied: $message", HttpStatus.UNAUTHORIZED.value())