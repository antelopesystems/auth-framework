package com.antelopesystem.authframework.authentication.method.usernamepassword

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.IllegalStateException

class UsernamePasswordAuthenticationMethodHandlerImpl(
        private val crudHandler: CrudHandler
) : AuthenticationMethodHandler {
    override val method: AuthenticationMethod
        get() = AuthenticationMethod.UsernamePassword

    override fun isPasswordBased(): Boolean = true

    override fun getEntityMethod(payload: AuthenticationRequestPayload): AuthenticatedEntityAuthenticationMethod? {
        try {
            return crudHandler.showBy(where {
                "param1" Equal payload.username()
                "method" Equal AuthenticationMethod.UsernamePassword
                "entity.type" Equal payload.type
            }, AuthenticatedEntityAuthenticationMethod::class.java)
                    .execute()
        } catch(e: IllegalStateException) {
            throw AuthenticationMethodException(e)
        }

    }

    override fun doLogin(payload: AuthenticationRequestPayload, method: AuthenticatedEntityAuthenticationMethod) {
        try {
            val usernameMatches = payload.username() == method.username()
            if (!usernameMatches) {
                throw LoginFailedException("Username is invalid")
            }
            val passwordMatches = passwordEncoder.matches(payload.password(), method.password())
            if (!passwordMatches) {
                throw LoginFailedException("Password is invalid")
            }
        } catch (e: IllegalStateException) {
            throw LoginFailedException(e)
        }
    }

    override fun doRegister(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod {
        try {
            val method = AuthenticatedEntityAuthenticationMethod(entity, AuthenticationMethod.UsernamePassword)
            method.username(payload.username())
            method.password(passwordEncoder.encode(payload.password()))
            return method
        } catch (e: IllegalStateException) {
            throw RegistrationFailedException(e)
        }
    }

    private fun AuthenticatedEntityAuthenticationMethod.username(username: String) {
        this.param1 = username
    }

    private fun AuthenticatedEntityAuthenticationMethod.password(password: String) {
        this.param2 = password
    }

    private fun AuthenticatedEntityAuthenticationMethod.username() = this.param1

    private fun AuthenticatedEntityAuthenticationMethod.password() = this.param2

    private fun AuthenticationRequestPayload.username() = (this.bodyMap["username"] ?: throw error("Username not specified")).toString()

    private fun AuthenticationRequestPayload.password() = (this.bodyMap["password"] ?: throw error("Password not specified")).toString()

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }
}