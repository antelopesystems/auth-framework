package com.antelopesystem.authframework.authentication.usernamepassword

import com.antelopesystem.authframework.authentication.AbstractAuthenticationTypeHandler
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

class UsernamePasswordTypeHandlerImpl(
        private val crudHandler: CrudHandler,
        securitySettingsHandler: SecuritySettingsHandler
) : AbstractAuthenticationTypeHandler(securitySettingsHandler) {
    override val type: AuthenticationType
        get() = AuthenticationType.UsernamePassword

    override fun getEntity(payload: AuthenticationPayload): AuthenticatedEntity? {
        val loginPayload = LoginPayload(payload)
        return crudHandler.showBy(where {
            "username" Equal loginPayload.username
            "type" Equal payload.type
        }, AuthenticatedEntity::class.java)
                .execute()
    }

    override fun doLogin(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        val loginPayload = LoginPayload(payload)

        val passwordMatches = passwordEncoder.matches(loginPayload.password, entity.password)
        if(!passwordMatches) {
            throw LoginFailedException("Password is invalid")
        }
    }

    override fun doRegister(payload: AuthenticationPayload, authenticatedEntity: AuthenticatedEntity): AuthenticatedEntity {
        val loginPayload = RegistrationPayload(payload)

        authenticatedEntity.username = loginPayload.username
        authenticatedEntity.password = passwordEncoder.encode(loginPayload.password)

        return authenticatedEntity
    }

    private class LoginPayload(val payload: AuthenticationPayload)  {
        val username: String get() = (payload.bodyMap["username"] ?: LoginFailedException("Username not specified")).toString()
        val password: String get() = (payload.bodyMap["password"] ?: LoginFailedException("Password not specified")).toString()
    }

    private class RegistrationPayload(val payload: AuthenticationPayload)  {
        val username: String get() = (payload.bodyMap["username"] ?: RegistrationFailedException("Username not specified")).toString()
        val password: String get() = (payload.bodyMap["password"] ?: RegistrationFailedException("Password not specified")).toString()
    }

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }
}