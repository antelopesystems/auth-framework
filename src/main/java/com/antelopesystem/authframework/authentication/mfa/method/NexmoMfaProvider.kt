package com.antelopesystem.authframework.authentication.mfa.method

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.integrations.NexmoClientProvider
import com.antelopesystem.authframework.integrations.NexmoException
import org.springframework.stereotype.Component

class NexmoMfaProvider(
        private val nexmoClientProvider: NexmoClientProvider
) : MfaProvider {
    override val type: MfaType
        get() = MfaType.Nexmo

    override fun setup(payload: MethodRequestPayload, entity: AuthenticatedEntity): EntityMfaMethod {
        return EntityMfaMethod(
                entity,
                MfaType.Nexmo,
                payload.telephonePrefix(),
                payload.telephone()
        )
    }

    override fun issue(method: EntityMfaMethod) {
        val client = nexmoClientProvider.getNexmoClient(method.entity.type)
        try {
            client.requestVerification(method.telephonePrefix() + method.telephone())
        } catch(e: NexmoException) {
            throw e
        } catch(e: Exception) {
            throw e
        } // todo
    }

    override fun validate(code: String, method: EntityMfaMethod) {
        val client = nexmoClientProvider.getNexmoClient(method.entity.type)
        try {
            val result = client.validateVerification(method.telephonePrefix() + method.telephone(), code)
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

private fun EntityMfaMethod.telephonePrefix(): String {
    return this.param1
}

private fun EntityMfaMethod.telephone(): String {
    return this.param2
}

private fun MethodRequestPayload.telephonePrefix() = (this.parameters["telephonePrefix"] ?: throw error("Telephone prefix not specified")).toString()

private fun MethodRequestPayload.telephone() = (this.parameters["telephone"] ?: throw error("Telephone not specified")).toString()

private fun MethodRequestPayload.code() = (this.parameters["code"] ?: throw error("Code not specified")).toString()