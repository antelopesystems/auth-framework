package com.antelopesystem.authframework.authentication.method.usernamepassword

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import kotlin.IllegalStateException

@Component
class UsernamePasswordAuthenticationMethodHandlerImpl(
        private val crudHandler: CrudHandler,
        private val securitySettingsHandler: SecuritySettingsHandler
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

    override fun initializeRegistration(payload: MethodRequestPayload): CustomParamsDTO {
        return CustomParamsDTO(
                payload.username(),
                passwordEncoder.encode(payload.password())
        )
    }

    override fun doRegister(payload: MethodRequestPayload, params: CustomParamsDTO, entity: AuthenticatedEntity): EntityAuthenticationMethod {
        try {
            val method = EntityAuthenticationMethod(entity, AuthenticationMethod.UsernamePassword)
            method.username(params.username())
            method.password(params.password())
            refreshPasswordExpiryTime(method)
            return method
        } catch (e: IllegalStateException) {
            throw RegistrationFailedException(e)
        }
    }

    override fun getUsernameFromPayload(payload: MethodRequestPayload): String {
        return payload.username()
    }

    override fun changePassword(newPassword: String, method: EntityAuthenticationMethod) {
        method.password(passwordEncoder.encode(newPassword))
        refreshPasswordExpiryTime(method)
    }

    override fun checkPassword(payload: MethodRequestPayload, method: EntityAuthenticationMethod): Boolean {
        return passwordEncoder.matches(payload.password(), method.password())
    }

    override fun isPasswordExpired(method: EntityAuthenticationMethod): Boolean {
        if(method.param3.isBlank()) return false
        return method.passwordExpiryTime().before(Date())
    }

    private fun refreshPasswordExpiryTime(method: EntityAuthenticationMethod) {
        val securitySettings = securitySettingsHandler.getSecuritySettings(method.entity.type)
        method.passwordExpiryTime(Date(System.currentTimeMillis() + securitySettings.passwordExpiryDays * 24L * 60L * 60L * 1000L))
    }

    private fun EntityAuthenticationMethod.username(username: String) {
        this.param1 = username
    }

    private fun EntityAuthenticationMethod.password(password: String) {
        this.param2 = password
    }

    private fun EntityAuthenticationMethod.username() = this.param1

    private fun EntityAuthenticationMethod.password() = this.param2

    private fun CustomParamsDTO.username() = this.param1

    private fun CustomParamsDTO.password() = this.param2

    private fun EntityAuthenticationMethod.passwordExpiryTime() = Date(this.param3.toLong())

    private fun EntityAuthenticationMethod.passwordExpiryTime(time: Date) = time.time.toString()


    private fun MethodRequestPayload.username() = (this.parameters["username"] ?: throw error("Username not specified")).toString()

    private fun MethodRequestPayload.password() = (this.parameters["password"] ?: throw error("Password not specified")).toString()

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }
}