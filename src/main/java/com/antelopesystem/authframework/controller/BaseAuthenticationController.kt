package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.method.enums.AuthenticationMethod
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

abstract class BaseAuthenticationController(
        private val objectType: String
): BaseController() {

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @PostMapping("/login/initialize")
    fun initializeLogin(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeLogin(MethodRequestPayload(objectType, mapOf(), body), tokenType)
        }
    }

    @PostMapping("/login")
    fun doLogin(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doLogin(MethodRequestPayload(objectType, mapOf(), body), tokenType)
        }
    }

    @PostMapping("/register/initialize")
    fun initializeRegistration(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeRegistration(MethodRequestPayload(objectType, mapOf(), body), tokenType)
        }
    }

    @PostMapping("/register")
    fun doRegister(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doRegister(MethodRequestPayload(objectType, mapOf(), body), tokenType)
        }
    }
}

