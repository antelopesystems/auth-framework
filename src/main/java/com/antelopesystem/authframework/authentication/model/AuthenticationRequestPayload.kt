package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthenticationRequestPayload (
        val type: String,
        val authenticationMethod: AuthenticationMethod,
        val queryParameters: Map<String, Any>,
        val body: String,
        val tokenType: TokenType
) {
    val bodyMap: Map<String, Any> = try {
        Gson().fromJson(body, object : TypeToken<Map<String, Any>>() {}.type)
    } catch(e: Exception) {
        emptyMap()
    }
}