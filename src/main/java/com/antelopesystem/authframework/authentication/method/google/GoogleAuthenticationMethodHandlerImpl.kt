package com.antelopesystem.authframework.authentication.method.google

import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.CustomParamsDTO
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.authentication.model.EntityAuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.stereotype.Component

@Component
class GoogleAuthenticationMethodHandlerImpl(
        private val settingsHandler: SecuritySettingsHandler,
        private val crudHandler: CrudHandler
) : AuthenticationMethodHandler {

    override val method: AuthenticationMethod
        get() = AuthenticationMethod.Google

    override fun isSupportedForPayload(payload: MethodRequestPayload): Boolean = try {
        payload.idToken()
        true
    } catch(e: Exception) { false }

    override fun getUsernameFromPayload(payload: MethodRequestPayload): String {
        return payload.idToken()
    }

    override fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod) {
        val idToken = validateIdToken(payload.idToken())
        if(idToken.payload.subject != method.uid()) {
            throw LoginFailedException("Login failed")
        }
    }

    override fun doRegister(payload: MethodRequestPayload, params: CustomParamsDTO, entity: Entity): EntityAuthenticationMethod {
        val idToken = validateIdToken(payload.idToken())
        return EntityAuthenticationMethod(
                entity,
                AuthenticationMethod.Google,
                idToken.payload.subject,
                idToken.payload.email
        )
    }

    override fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod? {
        val idToken = validateIdToken(payload.idToken())
        return crudHandler.showBy(where {
            "param1" Equal idToken.payload.subject
            "method" Equal AuthenticationMethod.Google
            "entity.type" Equal payload.type
        }, EntityAuthenticationMethod::class.java)
                .execute()
    }

    override fun getUsername(method: EntityAuthenticationMethod): String {
        return method.email()
    }

    private fun validateIdToken(idToken: String): GoogleIdToken {
        val clientId: String = settingsHandler.getSecuritySettings("User").google.clientId
        if(clientId.isBlank()) {
            error("Client ID is empty")
        }
        val token: GoogleIdToken = try {
            tokenVerifier.verify(idToken)
        } catch (e: Exception) {
            error("Token could not be verified")
        }

        if (!token.verifyAudience(listOf(clientId))) {
            error("Client ID provided in token does not match the local client ID")
        }
        return token
    }

    companion object {
        private val transport: HttpTransport = NetHttpTransport()
        private val jsonFactory: JsonFactory = JacksonFactory()

        private val tokenVerifier = GoogleIdTokenVerifier(transport, jsonFactory)
    }
}

private fun MethodRequestPayload.idToken() = (this.parameters["idToken"] ?: throw error("ID Token not specified")).toString()

private fun EntityAuthenticationMethod.uid() = this.param1

private fun EntityAuthenticationMethod.email() = this.param2

private fun EntityAuthenticationMethod.uid(uid: String) {
    this.param1 = uid
}

private fun EntityAuthenticationMethod.email(email: String) {
    this.param2 = email
}