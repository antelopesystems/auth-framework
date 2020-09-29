package com.antelopesystem.authframework.authentication.method.enums

enum class AuthenticationMethod(
        val registrationInitializationRequired: Boolean = false,
        val loginInitializationRequired: Boolean = false, // todo: enforce
        val passwordBased: Boolean = false,

        /**
         * Whether or not this integration supports registration on login and login on registration
         */
        val canCrossActions:  Boolean = true
) {
    UsernamePassword(
            passwordBased = true
    ), Nexmo(
            registrationInitializationRequired = true,
            loginInitializationRequired = true
    ), Authenticator(
            registrationInitializationRequired = true,
            canCrossActions = false
    ), Google, Facebook
}