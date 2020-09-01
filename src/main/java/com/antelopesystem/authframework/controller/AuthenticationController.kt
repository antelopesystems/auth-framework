package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.AuthenticationRequestPayload
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

abstract class AuthenticationController(
        private val authenticationService: AuthenticationService,
        private val objectType: String
): BaseController() {

    @PostMapping("/{authenticationMethod}/login/initialize")
    fun initializeLogin(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeLogin(AuthenticationRequestPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/login")
    fun doLogin(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doLogin(AuthenticationRequestPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/register/initialize")
    fun initializeRegistration(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeRegistration(AuthenticationRequestPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }

    @PostMapping("/{authenticationMethod}/register")
    fun doRegister(@PathVariable authenticationMethod: AuthenticationMethod, @RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doRegister(AuthenticationRequestPayload(objectType, authenticationMethod, mapOf(), body, tokenType))
        }
    }
}

