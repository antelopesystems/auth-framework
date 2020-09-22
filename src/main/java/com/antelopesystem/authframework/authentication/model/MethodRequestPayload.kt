package com.antelopesystem.authframework.authentication.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MethodRequestPayload(
        val type: String,
        val queryParameters: Map<String, Array<String>>,
        val body: String?
) {
    val parameters: Map<String, Any> = try {
        val map: MutableMap<String, Any> = Gson().fromJson(body, object : TypeToken<MutableMap<String, Any>>() {}.type)
        map.putAll(queryParameters.map { it.key to it.value.first() })
        map
    } catch(e: Exception) {
        emptyMap()
    }

    override fun toString(): String {
        return "MethodRequestPayload(type='$type', queryParameters=$queryParameters, body=$body, parameters=$parameters)"
    }
}