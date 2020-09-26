package com.antelopesystem.authframework.authentication.mfa.provider

import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaType
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.nexmo.NexmoClientProvider
import com.antelopesystem.authframework.integrations.nexmo.exception.NexmoException
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import org.springframework.stereotype.Component

@Component
class NexmoMfaProvider(
        private val nexmoClientProvider: NexmoClientProvider,
        private val securitySettingsHandler: SecuritySettingsHandler
) : MfaProvider {
    override val type: MfaType
        get() = MfaType.Nexmo

    override fun setup(payload: MethodRequestPayload, entity: Entity): CustomParamsDTO {
        return CustomParamsDTO(
                payload.telephonePrefix(),
                payload.telephone()
        )
    }

    override fun issue(entity: Entity, params: CustomParamsDTO) {
        val client = nexmoClientProvider.getNexmoClient(entity.type)
        try {
            client.requestVerification(params.telephonePrefix() + params.telephone())
        } catch(e: NexmoException) {
            throw e
        } catch(e: Exception) {
            throw e
        } // todo
    }

    override fun validate(code: String, entity: Entity, params: CustomParamsDTO) {
        val client = nexmoClientProvider.getNexmoClient(entity.type)
        try {
            val result = client.validateVerification(params.telephonePrefix() + params.telephone(), code)
            if(!result) {
                error("Invalid code")
            }
        } catch(e: NexmoException) {
            throw e
        } catch(e: Exception) {
            throw e
        }
    }
}

private fun CustomParamsDTO.telephonePrefix(): String {
    return this.param1
}

private fun CustomParamsDTO.telephone(): String {
    return this.param2
}

private fun EntityMfaMethod.telephonePrefix(): String {
    return this.param1
}

private fun EntityMfaMethod.telephone(): String {
    return this.param2
}

private fun MethodRequestPayload.telephonePrefix() = (this.parameters["telephonePrefix"] ?: throw error("Telephone prefix not specified")).toString()

private fun MethodRequestPayload.telephone() = (this.parameters["telephone"] ?: throw error("Telephone not specified")).toString()

private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()