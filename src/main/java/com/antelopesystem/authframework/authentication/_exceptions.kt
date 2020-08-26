package com.antelopesystem.authframework.authentication

abstract class AuthenticationException(message: String) : RuntimeException(message)

class LoginFailedException(message: String) : AuthenticationException(message)

class RegistrationFailedException(message: String) : AuthenticationException(message)