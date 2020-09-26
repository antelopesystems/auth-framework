package com.antelopesystem.authframework.authentication.mfa.provider

import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaType
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.entity.EntityHandler
import com.antelopesystem.authframework.integrations.AuthenticatorClientProvider
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class AuthenticatorMfaProvider(
        private val authenticatorClientProvider: AuthenticatorClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler,
        private val entityHandler: EntityHandler
) : MfaProvider {
    override val type: MfaType
        get() = MfaType.Authenticator

    override fun isSupportedForType(entityType: String): Boolean {
        return securitySettingsHandler.getSecuritySettings(entityType).authenticatorMfaEnabled
    }

    override fun setup(payload: MethodRequestPayload, entity: Entity): CustomParamsDTO {
        val client = authenticatorClientProvider.getAuthenticatorClient(entity.type)
        val response = client.setup(entityHandler.getEntityUsername(entity))
        return CustomParamsDTO(
                response.key,
                response.keyUrl
        )
    }

    override fun validate(code: String, entity: Entity, params: CustomParamsDTO) {
        val client = authenticatorClientProvider.getAuthenticatorClient(entity.type)
        val result = client.validate(params.key(), code.toInt())
        if(!result) {
            error("Invalid code")
        }
    }
}

private fun EntityMfaMethod.key(): String {
    return this.param1
}

private fun CustomParamsDTO.key(): String {
    return this.param1
}

private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()