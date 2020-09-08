package com.antelopesystem.authframework.authentication.method.nexmo

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where

class NexmoAuthenticationMethodHandlerImpl(
        private val crudHandler: CrudHandler,
        private val nexmoClientProvider: NexmoClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AuthenticationMethodHandler {
    override val method: AuthenticationMethod
        get() = AuthenticationMethod.Nexmo

    override fun isPasswordBased(): Boolean = false

    override fun isSupportedForType(type: String): Boolean = securitySettingsHandler.getSecuritySettings(type).nexmoAuthenticationEnabled

    // todo cleanTelephone
    // telephone, telephonePrefix
    override fun getEntityMethod(payload: AuthenticationRequestPayload): AuthenticatedEntityAuthenticationMethod? {
        try {
            return crudHandler.showBy(where {
                "param1" Equal payload.telephonePrefix()
                "param2" Equal payload.telephone()
                "method" Equal AuthenticationMethod.Nexmo
                "entity.type" Equal payload.type
            }, AuthenticatedEntityAuthenticationMethod::class.java)
                    .execute()
        } catch(e: IllegalStateException) {
            throw AuthenticationMethodException(e)
        }
    }

    override fun initializeLogin(payload: AuthenticationRequestPayload, method: AuthenticatedEntityAuthenticationMethod) {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(method.telephonePrefix() + method.telephone())
        } catch(e: NexmoException) {
            throw LoginFailedException(e)
        } catch(e: Exception) {
            throw LoginFailedException(e)
        }

    }

    override fun doLogin(payload: AuthenticationRequestPayload, method: AuthenticatedEntityAuthenticationMethod) {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            val result = client.validateVerification(method.telephonePrefix() + method.telephone(), payload.code())
            if(!result) {
                throw LoginFailedException("Invalid code")
            }
        } catch(e: NexmoException) {
            throw LoginFailedException(e)
        } catch(e: Exception) {
            throw LoginFailedException(e)
        }
    }

    override fun initializeRegistration(payload: AuthenticationRequestPayload) {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(payload.telephonePrefix() + payload.telephone())
        } catch(e: NexmoException) {
            throw RegistrationFailedException(e)
        } catch(e: Exception) {
            throw RegistrationFailedException(e)
        }

    }

    override fun doRegister(payload: AuthenticationRequestPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            val fullTelephone = payload.telephonePrefix() + payload.telephone()
            val result = client.validateVerification(fullTelephone, payload.code())
            if(!result) {
                throw RegistrationFailedException("Invalid code")
            }
        } catch(e: NexmoException) {
            throw RegistrationFailedException(e)
        } catch(e: Exception) {
            throw RegistrationFailedException(e)
        }

        val method = AuthenticatedEntityAuthenticationMethod(entity, AuthenticationMethod.Nexmo)
        method.telephonePrefix(payload.telephonePrefix())
        method.telephone(payload.telephone())
        return method
    }

    private fun AuthenticationRequestPayload.telephonePrefix() = (this.bodyMap["telephonePrefix"] ?: throw error("Telephone prefix not specified")).toString()

    private fun AuthenticationRequestPayload.telephone() = (this.bodyMap["telephone"] ?: throw error("Telephone not specified")).toString()

    private fun AuthenticationRequestPayload.code() = (this.bodyMap["telephone"] ?: throw error("Telephone not specified")).toString()

    private fun AuthenticatedEntityAuthenticationMethod.telephonePrefix(): String {
        return this.param2
    }

    private fun AuthenticatedEntityAuthenticationMethod.telephone(): String {
        return this.param1
    }

    private fun AuthenticatedEntityAuthenticationMethod.telephonePrefix(telephonePrefix: String) {
        this.param1 = telephonePrefix
    }

    private fun AuthenticatedEntityAuthenticationMethod.telephone(telephone: String) {
        this.param2 = telephone
    }
}