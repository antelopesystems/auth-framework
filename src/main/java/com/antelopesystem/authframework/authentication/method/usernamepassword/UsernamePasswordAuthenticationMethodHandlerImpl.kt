package com.antelopesystem.authframework.authentication.method.usernamepassword

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
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

    override fun isSupportedForPayload(payload: MethodRequestPayload): Boolean = try {
        payload.username()
        true
    } catch(e: Exception) { false}

    override fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod? {
        try {
            return crudHandler.showBy(where {
                "param1" Equal payload.username()
                "method" Equal AuthenticationMethod.UsernamePassword
                "entity.type" Equal payload.type
            }, EntityAuthenticationMethod::class.java)
                    .execute()
        } catch(e: IllegalStateException) {
            throw AuthenticationMethodException(e)
        }
    }

    override fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod) {
        try {
            val usernameMatches = payload.username() == method.username()
            if (!usernameMatches) {
                throw LoginFailedException("Username is invalid")
            }
            val passwordMatches = checkPassword(payload, method)
            if (!passwordMatches) {
                throw LoginFailedException("Password is invalid")
            }
        } catch (e: IllegalStateException) {
            throw LoginFailedException(e)
        }
    }

    override fun doRegister(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityAuthenticationMethod {
        try {
            val method = EntityAuthenticationMethod(entity, AuthenticationMethod.UsernamePassword)
            method.username(payload.username())
            method.password(passwordEncoder.encode(payload.password()))
            return method
        } catch (e: IllegalStateException) {
            throw RegistrationFailedException(e)
        }
    }

    override fun changePassword(newPassword: String, method: EntityAuthenticationMethod) {
        method.password(passwordEncoder.encode(newPassword))
    }

    override fun checkPassword(payload: MethodRequestPayload, method: EntityAuthenticationMethod): Boolean {
        return passwordEncoder.matches(payload.password(), method.password())
    }

    private fun EntityAuthenticationMethod.username(username: String) {
        this.param1 = username
    }

    private fun EntityAuthenticationMethod.password(password: String) {
        this.param2 = password
    }

    private fun EntityAuthenticationMethod.username() = this.param1

    private fun EntityAuthenticationMethod.password() = this.param2

    private fun MethodRequestPayload.username() = (this.bodyMap["username"] ?: throw error("Username not specified")).toString()

    private fun MethodRequestPayload.password() = (this.bodyMap["password"] ?: throw error("Password not specified")).toString()

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }
}