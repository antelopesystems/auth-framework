package com.antelopesystem.authframework.authentication.method.enums

enum class AuthenticationMethod(
        val registrationInitializationRequired: Boolean = false,
        val loginInitializationRequired: Boolean = false,
        val passwordBased: Boolean = false
) {
    UsernamePassword(
            passwordBased = true
    ), Nexmo(
            registrationInitializationRequired = true,
            loginInitializationRequired = true
    ), Authenticator(
            registrationInitializationRequired = true
    ), Google, Facebook
}