package com.antelopesystem.authframework.authentication.usernamepassword

import com.antelopesystem.authframework.authentication.AbstractAuthenticationTypeHandler
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UsernamePasswordTypeHandlerImpl(
        private val crudHandler: CrudHandler
) : AbstractAuthenticationTypeHandler() {
    override val type: AuthenticationType
        get() = AuthenticationType.UsernamePassword

    override fun isPasswordBased(): Boolean = true

    override fun getEntityAuthenticationType(payload: AuthenticationPayload): AuthenticatedEntityAuthenticationMethod? {
        val loginPayload = LoginPayload(payload)
        return crudHandler.showBy(where {
            "param1" Equal loginPayload.username
            "type" Equal AuthenticationType.UsernamePassword
            "entity.type" Equal payload.type
        }, AuthenticatedEntityAuthenticationMethod::class.java)
                .execute()
    }

    override fun doLogin(payload: AuthenticationPayload, method: AuthenticatedEntityAuthenticationMethod) {
        val loginPayload = LoginPayload(payload)

        val passwordMatches = passwordEncoder.matches(loginPayload.password, method.password())
        if (!passwordMatches) {
            throw LoginFailedException("Password is invalid")
        }
    }

    override fun doRegister(payload: AuthenticationPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod {
        val loginPayload = RegistrationPayload(payload)

        val method = AuthenticatedEntityAuthenticationMethod(entity, AuthenticationType.UsernamePassword)
        method.username(loginPayload.username)
        method.password(passwordEncoder.encode(loginPayload.password))
        return method
    }

    private fun AuthenticatedEntityAuthenticationMethod.username(username: String) {
        this.param1 = username
    }

    private fun AuthenticatedEntityAuthenticationMethod.password(password: String) {
        this.param2 = password
    }


    private fun AuthenticatedEntityAuthenticationMethod.username() = this.param1

    private fun AuthenticatedEntityAuthenticationMethod.password() = this.param2

    private class LoginPayload(val payload: AuthenticationPayload) {
        val username: String get() = (payload.bodyMap["username"] ?: LoginFailedException("Username not specified")).toString()
        val password: String get() = (payload.bodyMap["password"] ?: LoginFailedException("Password not specified")).toString()
    }

    private class RegistrationPayload(val payload: AuthenticationPayload) {
        val username: String get() = (payload.bodyMap["username"] ?: RegistrationFailedException("Username not specified")).toString()
        val password: String get() = (payload.bodyMap["password"] ?: RegistrationFailedException("Password not specified")).toString()
    }

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }
}