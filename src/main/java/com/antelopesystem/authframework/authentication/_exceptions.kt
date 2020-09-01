package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.filter.RequestFailedException
import org.springframework.http.HttpStatus

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