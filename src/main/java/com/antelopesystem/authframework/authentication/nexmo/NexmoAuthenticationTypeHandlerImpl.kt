package com.antelopesystem.authframework.authentication.nexmo

import com.antelopesystem.authframework.authentication.AbstractAuthenticationTypeHandler
import com.antelopesystem.authframework.authentication.GenericPayloadWrapper
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntityAuthenticationMethod
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where

class NexmoAuthenticationTypeHandlerImpl(
        private val crudHandler: CrudHandler,
        private val nexmoClientProvider: NexmoClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler
) : AbstractAuthenticationTypeHandler() {
    override val type: AuthenticationType
        get() = AuthenticationType.Nexmo

    override fun isPasswordBased(): Boolean = false

    override fun isSupportedForType(type: String): Boolean = securitySettingsHandler.getSecuritySettings(type).nexmoAuthenticationEnabled

    // todo cleanTelephone
    override fun getEntityAuthenticationType(payload: AuthenticationPayload): AuthenticatedEntityAuthenticationMethod? {
        val genericPayload = LoginPayload(payload)
        return crudHandler.showBy(where {
            "param1" Equal genericPayload.telephonePrefix
            "param2" Equal genericPayload.telephone
            "type" Equal AuthenticationType.Nexmo
            "entity.type" Equal payload.type
        }, AuthenticatedEntityAuthenticationMethod::class.java)
                .execute()
    }

    override fun initializeLogin(payload: AuthenticationPayload, entity: AuthenticatedEntityAuthenticationMethod) {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(entity.telephone())
        } catch(e: NexmoException) {
            throw LoginFailedException(e)
        }
    }

    override fun doLogin(payload: AuthenticationPayload, entity: AuthenticatedEntityAuthenticationMethod) {
        val loginPayload = LoginPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            val result = client.validateVerification(entity.telephone(), loginPayload.code)
            if(!result) {
                throw LoginFailedException("Invalid code")
            }
        } catch(e: NexmoException) {
            throw LoginFailedException(e)
        }
    }

    override fun initializeRegistration(payload: AuthenticationPayload) {
        val registrationPayload = InitializeRegistrationPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(registrationPayload.telephonePrefix + registrationPayload.telephone)
        } catch(e: NexmoException) {
            throw RegistrationFailedException(e)
        }
    }

    override fun doRegister(payload: AuthenticationPayload, entity: AuthenticatedEntity): AuthenticatedEntityAuthenticationMethod {
        val registrationPayload = RegistrationPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        val fullTelephone = registrationPayload.telephonePrefix + registrationPayload.telephone
        try {
            val result = client.validateVerification(fullTelephone, registrationPayload.code)
            if(!result) {
                throw RegistrationFailedException("Invalid code")
            }
        } catch(e: NexmoException) {
            throw RegistrationFailedException(e)
        }

        val method = AuthenticatedEntityAuthenticationMethod(entity, AuthenticationType.Nexmo)
        method.telephone(registrationPayload.telephonePrefix, registrationPayload.telephone)
        return method
    }

    private open class InitializeLoginPayload(payload: AuthenticationPayload) : GenericPayloadWrapper(payload)  {
        val telephonePrefix: String get() = (payload.bodyMap["telephonePrefix"] ?: throw LoginFailedException("Telephone prefix not specified")).toString()
        val telephone: String get() = (payload.bodyMap["telephone"] ?: throw LoginFailedException("Telephone not specified")).toString()
    }

    private class LoginPayload(payload: AuthenticationPayload) : InitializeLoginPayload(payload) {
        val code: String get() = (payload.bodyMap["code"] ?: throw LoginFailedException("Code not specified")).toString()
    }

    private open class InitializeRegistrationPayload(payload: AuthenticationPayload) : GenericPayloadWrapper(payload)  {
        val telephonePrefix: String get() = (payload.bodyMap["telephonePrefix"] ?: throw RegistrationFailedException("Telephone prefix not specified")).toString()
        val telephone: String get() = (payload.bodyMap["telephone"] ?: throw RegistrationFailedException("Telephone not specified")).toString()
    }

    private class RegistrationPayload(payload: AuthenticationPayload) : InitializeLoginPayload(payload) {
        val code: String get() = (payload.bodyMap["code"] ?: throw RegistrationFailedException("Code not specified")).toString()
    }

    private fun AuthenticatedEntityAuthenticationMethod.telephone(): String {
        return this.param1 + this.param2
    }

    private fun AuthenticatedEntityAuthenticationMethod.telephone(telephonePrefix: String, telephone: String) {
        this.param1 = telephonePrefix
        this.param2 = telephone
    }
}