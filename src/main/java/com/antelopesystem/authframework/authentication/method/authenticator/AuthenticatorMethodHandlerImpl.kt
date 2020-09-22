package com.antelopesystem.authframework.authentication.method.authenticator

import com.antelopesystem.authframework.authentication.AuthenticationMethodException
import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.RegistrationFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.AuthenticatorClient
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where

class AuthenticatorMethodHandlerImpl(
        private val crudHandler: CrudHandler
) : AuthenticationMethodHandler {

    private val client = AuthenticatorClient("Demo")
    override val method: AuthenticationMethod
        get() = AuthenticationMethod.Authenticator

    override fun isPasswordBased(): Boolean = false

    override fun isSupportedForPayload(payload: MethodRequestPayload): Boolean = try {
        payload.authenticatorUsername()
        true
    } catch(e: Exception) { false }

    override fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod? {
        try {
            return crudHandler.showBy(where {
                "param1" Equal payload.authenticatorUsername()
                "method" Equal AuthenticationMethod.Authenticator
                "entity.type" Equal payload.type
            }, EntityAuthenticationMethod::class.java)
                    .execute()
        } catch(e: IllegalStateException) {
            throw AuthenticationMethodException(e)
        }
    }

    override fun initializeRegistration(payload: MethodRequestPayload): CustomParamsDTO {
        val response = client.setup(payload.authenticatorUsername())
        return CustomParamsDTO(
                response.key,
                response.keyUrl
        )
    }

    override fun getUsernameFromPayload(payload: MethodRequestPayload): String {
        return payload.authenticatorUsername()
    }

    override fun doRegister(payload: MethodRequestPayload, params: CustomParamsDTO, entity: AuthenticatedEntity): EntityAuthenticationMethod {
        try {
            val result = client.validate(params.key(), payload.code())
            if(!result) {
                error("Invalid code")
            }

            val method = EntityAuthenticationMethod(entity, AuthenticationMethod.Authenticator)
            method.authenticatorUsername(params.authenticatorUsername())
            method.key(params.key())
            method.keyUrl(params.keyUrl())
            return method
        } catch(e: Exception) {
            throw RegistrationFailedException(e)
        }
    }

    override fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod) {
        try {
            val result = client.validate(method.key(), payload.code())
            if(!result) {
                throw LoginFailedException("Invalid code")
            }
        } catch(e: Exception) {
            throw LoginFailedException(e)
        }
    }

    private fun MethodRequestPayload.authenticatorUsername() = (this.parameters["authenticatorUsername"] ?: throw error("Authenticator Username not specified")).toString()

    private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString().toInt()

    private fun EntityAuthenticationMethod.authenticatorUsername() = this.param1

    private fun EntityAuthenticationMethod.key() = this.param2

    private fun EntityAuthenticationMethod.keyUrl() = this.param3

    private fun CustomParamsDTO.authenticatorUsername() = this.param1

    private fun CustomParamsDTO.key() = this.param2

    private fun CustomParamsDTO.keyUrl() = this.param3

    private fun EntityAuthenticationMethod.authenticatorUsername(authenticatorUsername: String) {
        this.param1 = authenticatorUsername
    }

    private fun EntityAuthenticationMethod.key(secret: String) {
        this.param2 = secret
    }

    private fun EntityAuthenticationMethod.keyUrl(keyUrl: String) {
        this.param3 = keyUrl
    }
}