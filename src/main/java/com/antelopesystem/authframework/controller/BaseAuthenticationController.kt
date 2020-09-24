package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.annotations.BypassPasswordExpiredCheck
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.token.type.enums.TokenType
import com.antelopesystem.authframework.util.getUserInfo
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletRequest

abstract class BaseAuthenticationController(
        private val objectType: String
): BaseController() {

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @PostMapping("/login/initialize")
    fun initializeLogin(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, request: HttpServletRequest): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeLogin(MethodRequestPayload(objectType, request.parameterMap, body), tokenType)
        }
    }

    @PostMapping("/login")
    fun doLogin(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, request: HttpServletRequest): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doLogin(MethodRequestPayload(objectType, request.parameterMap, body), tokenType)
        }
    }

    @PostMapping("/register/initialize")
    fun initializeRegistration(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, request: HttpServletRequest): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.initializeRegistration(MethodRequestPayload(objectType, request.parameterMap, body), tokenType)
        }
    }

    @PostMapping("/register")
    fun doRegister(@RequestBody(required = false) body: String, @RequestParam tokenType: TokenType, request: HttpServletRequest): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.doRegister(MethodRequestPayload(objectType, request.parameterMap, body), tokenType)
        }
    }

    @PostMapping("/forgot-password")
    fun initializeForgotPassword(@RequestBody(required = false) body: String, request: HttpServletRequest): ResultRO<*> {
        return wrapVoidResult {
            authenticationService.initializeForgotPassword(MethodRequestPayload(objectType, request.parameterMap, body))
        }
    }

    @PostMapping("/forgot-password/redeem/{token}")
    fun redeemForgotPasswordToken(@PathVariable token: String, @RequestParam newPassword: String): ResultRO<*> {
        return wrapVoidResult {
            authenticationService.redeemForgotPasswordToken(token, newPassword, objectType)
        }
    }

    @PostMapping("/change-password")
    @BypassPasswordExpiredCheck
    fun changePassword(@RequestBody(required = false) body: String, @RequestParam newPassword: String, request: HttpServletRequest): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.changePassword(MethodRequestPayload(objectType, request.parameterMap, body), newPassword, objectType)
        }
    }

    @GetMapping("/methods")
    fun getAvailableMethodsForEntity(): ResultRO<*> {
        return wrapResult {
            return@wrapResult authenticationService.getAvailableMethods(objectType)
        }
    }
}

