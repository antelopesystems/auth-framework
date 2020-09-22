package com.antelopesystem.authframework.authentication.mfa.method

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.AuthenticatorClientProvider
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class AuthenticatorMfaProvider(
        private val authenticatorClientProvider: AuthenticatorClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler
) : MfaProvider {
    override val type: MfaType
        get() = MfaType.Authenticator

    override fun isSupportedForType(entityType: String): Boolean {
        return securitySettingsHandler.getSecuritySettings(entityType).authenticatorMfaEnabled
    }

    override fun setup(payload: MethodRequestPayload, entity: AuthenticatedEntity): CustomParamsDTO {
        val client = authenticatorClientProvider.getAuthenticatorClient(entity.type)
        val response = client.setup("test")
        return CustomParamsDTO(
                response.key,
                response.keyUrl
        )
    }

    override fun validate(code: String, entity: AuthenticatedEntity, params: CustomParamsDTO) {
        val client = authenticatorClientProvider.getAuthenticatorClient(params.entity.type)
        val result = client.validate(params.key(), code.toInt())
        if(!result) {
            error("Invalid code")
        }
    }
}

private fun EntityMfaMethod.key(): String {
    return this.param1
}

private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()