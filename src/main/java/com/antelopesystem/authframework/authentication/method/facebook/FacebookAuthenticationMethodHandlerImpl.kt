package com.antelopesystem.authframework.authentication.method.facebook

import com.antelopesystem.authframework.authentication.LoginFailedException
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.*
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import com.google.gson.*
import com.mashape.unirest.http.Unirest
import org.springframework.stereotype.Component

@Component
class FacebookAuthenticationMethodHandlerImpl(
        private val settingsHandler: SecuritySettingsHandler,
        private val crudHandler: CrudHandler
) : AuthenticationMethodHandler {

    override val method: AuthenticationMethod
        get() = AuthenticationMethod.Facebook

    override fun isSupportedForPayload(payload: MethodRequestPayload): Boolean = try {
        payload.accessToken()
        true
    } catch(e: Exception) { false }

    override fun getUsernameFromPayload(payload: MethodRequestPayload): String {
        return payload.accessToken()
    }

    override fun doLogin(payload: MethodRequestPayload, method: EntityAuthenticationMethod) {
        val facebookDTO = getFacebookDTO(payload.accessToken())
        if(facebookDTO.id != method.uid()) {
            throw LoginFailedException("Login failed")
        }
    }

    override fun doRegister(payload: MethodRequestPayload, params: CustomParamsDTO, entity: Entity): EntityAuthenticationMethod {
        val facebookDTO = getFacebookDTO(payload.accessToken())
        return EntityAuthenticationMethod(
                entity,
                AuthenticationMethod.Facebook,
                facebookDTO.id,
                facebookDTO.email
        )
    }

    override fun getEntityMethod(payload: MethodRequestPayload): EntityAuthenticationMethod? {
        val facebookDTO = getFacebookDTO(payload.accessToken())
        return crudHandler.showBy(where {
            "param1" Equal facebookDTO.id
            "method" Equal AuthenticationMethod.Facebook
            "entity.type" Equal payload.type
        }, EntityAuthenticationMethod::class.java)
                .execute()
    }

    override fun getUsername(method: EntityAuthenticationMethod): String {
        return method.email()
    }

    private fun getFacebookDTO(accessToken: String): FacebookDTO {
        val response = Unirest
                .get("https://graph.facebook.com/v8.0/me")
                .field("access_token", accessToken)
                .field("fields", "id,email")
                .asString()
        val result: FacebookResponse = gson.fromJson(response.body, FacebookResponse::class.java)
        if (result.error != null) {
            error("Failed to retrieve data, error received: ${result.error.message}")
        }
        if(result.email.isNullOrBlank()) {
            error("Failed to retrieve data, Missing email in response")
        }

        return FacebookDTO(result.email, result.id)
    }

    companion object {
        private val gson = Gson()
    }
}

private fun MethodRequestPayload.accessToken() = (this.parameters["accessToken"] ?: throw error("ID Token not specified")).toString()

private fun EntityAuthenticationMethod.uid() = this.param1

private fun EntityAuthenticationMethod.email() = this.param2

private fun EntityAuthenticationMethod.uid(uid: String) {
    this.param1 = uid
}

private fun EntityAuthenticationMethod.email(email: String) {
    this.param2 = email
}

data class FacebookDTO(
        val email: String,
        val id: String
)

data class FacebookResponse(
        val email: String? = null,
        val id: String = "",
        val error: FacebookErrorResponse? = null
)

data class FacebookErrorResponse(
        val message: String? = null
)