package com.antelopesystem.authframework.authentication.nexmo

import com.antelopesystem.authframework.authentication.AbstractAuthenticationTypeHandler
import com.antelopesystem.authframework.authentication.GenericPayloadWrapper
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.controller.AuthenticationPayload
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where

class NexmoAuthenticationTypeHandlerImpl(
        private val crudHandler: CrudHandler,
        private val nexmoClientProvider: NexmoClientProvider
) : AbstractAuthenticationTypeHandler() {
    override val type: AuthenticationType
        get() = AuthenticationType.Nexmo

    // todo cleanTelephone
    override fun getEntity(payload: AuthenticationPayload): AuthenticatedEntity? {
        val genericPayload = LoginPayload(payload)
        return crudHandler.showBy(where {
            "telephonePrefix" Equal genericPayload.telephonePrefix
            "telephone" Equal genericPayload.telephone
            "type" Equal payload.type
        }, AuthenticatedEntity::class.java)
                .execute()
    }

    override fun initializeLogin(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        client.requestVerification(entity.fullTelephone)
    }

    override fun doLogin(payload: AuthenticationPayload, entity: AuthenticatedEntity) {
        val loginPayload = LoginPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        val result = client.validateVerification(entity.fullTelephone, loginPayload.code)
        if(!result) {
            throw LoginFailedException("Invalid code")
        }
    }

    override fun initializeRegistration(payload: AuthenticationPayload) {
        val registrationPayload = InitializeRegistrationPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        client.requestVerification(registrationPayload.telephonePrefix + registrationPayload.telephone)
    }

    override fun doRegister(payload: AuthenticationPayload, authenticatedEntity: AuthenticatedEntity): AuthenticatedEntity {
        val registrationPayload = RegistrationPayload(payload)
        val client = nexmoClientProvider.getNexmoClient(payload.type)
        val fullTelephone = registrationPayload.telephonePrefix + registrationPayload.telephone
        val result = client.validateVerification(fullTelephone, registrationPayload.code)
        if(!result) {
            throw LoginFailedException("Invalid code")
        }

        authenticatedEntity.username = fullTelephone
        authenticatedEntity.telephonePrefix = registrationPayload.telephonePrefix
        authenticatedEntity.telephone = registrationPayload.telephone

        return authenticatedEntity
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


}