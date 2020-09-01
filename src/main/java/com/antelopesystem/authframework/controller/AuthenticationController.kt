package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.enums.AuthenticationMethod
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.web.bind.annotation.*

@RequestMapping("/auth")
@RestController
class AuthenticationController(
        private val authenticationService: AuthenticationService
): BaseController() {

    private val objectType: String = "User"

    @PostMapping("/{authenticationMethod}/login/initialize")
    fun initializeLogin(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeLogin(AuthenticationPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/login")
    fun doLogin(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doLogin(AuthenticationPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/register/initialize")
    fun initializeRegistration(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeRegistration(AuthenticationPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/register")
    fun doRegister(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doRegister(AuthenticationPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }
}

open class AuthenticationPayload (
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