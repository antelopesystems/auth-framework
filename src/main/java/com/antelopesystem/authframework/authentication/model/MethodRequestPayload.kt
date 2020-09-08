package com.antelopesystem.authframework.authentication.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MethodRequestPayload(
        val type: String,
        val queryParameters: Map<String, Any>,
        val body: String
) {
    val bodyMap: Map<String, Any> = try {
        Gson().fromJson(body, object : TypeToken<Map<String, Any>>() {}.type)
    } catch(e: Exception) {
        emptyMap()
    }
}