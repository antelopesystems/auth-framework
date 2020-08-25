package com.antelopesystem.authframework.authentication

class LoginFailedException(message: String) : RuntimeException(message)

class RegistrationFailedException(message: String) : RuntimeException(message)