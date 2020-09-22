package com.antelopesystem.authframework.authentication.mfa.method

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.AuthenticatorClientProvider
import com.antelopesystem.authframework.integrations.NexmoClientProvider
import com.antelopesystem.authframework.integrations.NexmoException
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

class AuthenticatorMfaProvider(
        private val authenticatorClientProvider: AuthenticatorClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler
) : MfaProvider {
    override val type: MfaType
        get() = MfaType.Authenticator

    override fun isSupportedForType(entityType: String): Boolean {
        return securitySettingsHandler.getSecuritySettings(entityType).authenticatorMfaEnabled
    }

    override fun setup(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityMfaMethod {
        val client = authenticatorClientProvider.getAuthenticatorClient(entity.type)
        val response = client.setup("test")
        return EntityMfaMethod(
                entity,
                MfaType.Authenticator,
                response.key,
                response.keyUrl
        )
    }

    override fun validate(code: String, method: EntityMfaMethod) {
        val client = authenticatorClientProvider.getAuthenticatorClient(method.entity.type)
        val result = client.validate(method.key(), code.toInt())
        if(!result) {
            error("Invalid code")
        }
    }
}

private fun EntityMfaMethod.key(): String {
    return this.param1
}

private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()