package com.antelopesystem.authframework.authentication

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