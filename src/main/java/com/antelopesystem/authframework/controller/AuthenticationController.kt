package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.enums.AuthenticationType
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.web.bind.annotation.*

@RequestMapping("/auth")
@RestController
class AuthenticationController(
        private val authenticationService: AuthenticationService,
        private val objectType: String = "User"
): BaseController() {

    @PostMapping("/{authenticationType}/login/initialize")
    fun initializeLogin(@PathVariable authenticationType: AuthenticationType, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, parameters: Map<String, Any>): ResultRO<*> {
        return wrapResult {
            return@wrapResult AuthenticationPayload(objectType, authenticationType, parameters, body, tokenType)
        }
    }

    @PostMapping("/{authenticationType}/login")
    fun doLogin(@PathVariable authenticationType: AuthenticationType, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, parameters: Map<String, Any>): ResultRO<*> {
        return wrapResult {
            return@wrapResult AuthenticationPayload(objectType, authenticationType, parameters, body, tokenType)
        }
    }

    @PostMapping("/{authenticationType}/register/initialize")
    fun initializeRegistration(@PathVariable authenticationType: AuthenticationType, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, parameters: Map<String, Any>): ResultRO<*> {
        return wrapResult {
            return@wrapResult AuthenticationPayload(objectType, authenticationType, parameters, body, tokenType)
        }
    }

    @PostMapping("/{authenticationType}/register")
    fun doRegistration(@PathVariable authenticationType: AuthenticationType, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, parameters: Map<String, Any>): ResultRO<*> {
        return wrapResult {
            return@wrapResult AuthenticationPayload(objectType, authenticationType, parameters, body, tokenType)
        }
    }
}

open class AuthenticationPayload (
        val type: String,
        val authenticationType: AuthenticationType,
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