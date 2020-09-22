package com.antelopesystem.authframework.authentication.method.nexmo

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.NexmoClientProvider
import com.antelopesystem.authframework.integrations.NexmoException
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

    override fun isSupportedForPayload(payload: MethodRequestPayload): Boolean = try {
            payload.telephone()
            payload.telephonePrefix()
            true
        } catch(e: IllegalStateException) { false }

    // todo cleanTelephone
    // telephone, telephonePrefix
    override fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod? {
        try {
            return crudHandler.showBy(where {
                "param1" Equal payload.telephonePrefix()
                "param2" Equal payload.telephone()
                "method" Equal AuthenticationMethod.Nexmo
                "entity.type" Equal payload.type
            }, EntityAuthenticationMethod::class.java)
                    .execute()
        } catch(e: IllegalStateException) {
            throw AuthenticationMethodException(e)
        }
    }

    override fun initializeLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod): Any? {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(method.telephonePrefix() + method.telephone())
        } catch(e: NexmoException) {
            throw LoginFailedException(e)
        } catch(e: Exception) {
            throw LoginFailedException(e)
        }

        return null
    }

    override fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod) {
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

    override fun initializeRegistration(payload: MethodRequestPayload): Any? {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        try {
            client.requestVerification(payload.telephonePrefix() + payload.telephone())
        } catch(e: NexmoException) {
            throw RegistrationFailedException(e)
        } catch(e: Exception) {
            throw RegistrationFailedException(e)
        }

        return null
    }

    override fun getUsernameFromPayload(payload: MethodRequestPayload): String {
        return payload.telephonePrefix() + payload.telephone()
    }

    override fun doRegister(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityAuthenticationMethod {
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

        val method = EntityAuthenticationMethod(entity, AuthenticationMethod.Nexmo)
        method.telephonePrefix(payload.telephonePrefix())
        method.telephone(payload.telephone())
        return method
    }

    private fun MethodRequestPayload.telephonePrefix() = (this.parameters["telephonePrefix"] ?: throw error("Telephone prefix not specified")).toString()

    private fun MethodRequestPayload.telephone() = (this.parameters["telephone"] ?: throw error("Telephone not specified")).toString()

    private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()

    private fun EntityAuthenticationMethod.telephonePrefix(): String {
        return this.param1
    }

    private fun EntityAuthenticationMethod.telephone(): String {
        return this.param2
    }

    private fun EntityAuthenticationMethod.telephonePrefix(telephonePrefix: String) {
        this.param1 = telephonePrefix
    }

    private fun EntityAuthenticationMethod.telephone(telephone: String) {
        this.param2 = telephone
    }
}