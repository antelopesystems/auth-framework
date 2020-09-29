package com.antelopesystem.authframework.authentication.method.enums

enum class UsernameType {
    Email, Telephone, Username
}

enum class AuthenticationMethod(
        val registrationInitializationRequired: Boolean = false,
        val loginInitializationRequired: Boolean = false, // todo: enforce
        val passwordBased: Boolean = false,
        val usernameType: UsernameType,

        /**
         * Whether or not this integration supports registration on login and login on registration
         */
        val canCrossActions:  Boolean = true
) {
    UsernamePassword(
            passwordBased = true,
            usernameType = UsernameType.Username
    ),
    EmailPassword(
            passwordBased = true,
            usernameType = UsernameType.Email
    ),
    Nexmo(
            registrationInitializationRequired = true,
            loginInitializationRequired = true,
            usernameType = UsernameType.Telephone
    ), Authenticator(
            registrationInitializationRequired = true,
            canCrossActions = false,
            usernameType = UsernameType.Username
    ), Google(
            usernameType = UsernameType.Email
    ), Facebook(
            usernameType = UsernameType.Email
    )
}