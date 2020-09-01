package com.antelopesystem.authframework.authentication

import com.antelopesystem.authframework.authentication.filter.RequestFailedException
import org.springframework.http.HttpStatus

abstract class AuthenticationException(message: String) : RuntimeException(message) {
}

class LoginFailedException(message: String) : AuthenticationException(message) {
    constructor(cause: Throwable) : this(cause.message.toString()) {
        initCause(cause)
    }
}

class RegistrationFailedException(message: String) : AuthenticationException(message) {
    constructor(cause: Throwable) : this(cause.message.toString()) {
        initCause(cause)
    }
}

open class AccessDeniedException(message: String) : RequestFailedException("Access Denied: $message", HttpStatus.UNAUTHORIZED.value())