package com.antelopesystem.authframework.controller

import com.antelopesystem.authframework.authentication.annotations.BypassMfa
import com.antelopesystem.authframework.authentication.mfa.MfaService
import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.util.getUserInfo
import com.antelopesystem.crudframework.web.controller.BaseController
import com.antelopesystem.crudframework.web.ro.ResultRO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletRequest

abstract class BaseMfaController(
        private val objectType: String
) : BaseController() {

    @Autowired
    private lateinit var mfaService: MfaService

    @PostMapping("/{mfaType}/setup")
    fun setup(@PathVariable mfaType: MfaType, principal: Principal, request: HttpServletRequest, @RequestBody(required = false) body: String?) : ResultRO<*> {
        return wrapResult {
            return@wrapResult mfaService.setup(mfaType, MethodRequestPayload(objectType, request.parameterMap, body), principal.getUserInfo())
        }
    }

    @PostMapping("/{mfaType}/activate")
    fun activate(@PathVariable mfaType: MfaType, @RequestParam code: String, principal: Principal) : ResultRO<*> {
        return wrapVoidResult {
            mfaService.activate(mfaType, code, principal.getUserInfo())
        }
    }

    @PostMapping("/{mfaType}/deactivate")
    fun deactivate(@PathVariable mfaType: MfaType, principal: Principal) : ResultRO<*> {
        return wrapVoidResult {
            mfaService.deactivate(mfaType, principal.getUserInfo())
        }
    }

    @BypassMfa
    @PostMapping("/{mfaType}/issue")
    fun issue(@PathVariable mfaType: MfaType, principal: Principal) : ResultRO<*> {
        return wrapResult {
            mfaService.issue(mfaType, principal.getUserInfo())
        }
    }

    @BypassMfa
    @PostMapping("/{mfaType}/validate-to-token")
    fun validateToCurrentToken(@PathVariable mfaType: MfaType, @RequestParam code: String, principal: Principal) : ResultRO<*> {
        return wrapVoidResult {
            mfaService.validateToCurrentToken(mfaType, code, principal.getUserInfo())
        }
    }

    @GetMapping("/providers")
    fun getAvailableProvidersForUser(principal: Principal): ResultRO<*> {
        return wrapResult {
            return@wrapResult mfaService.getAvailableProviders(principal.getUserInfo())
        }
    }

    @GetMapping("/providers/enabled")
    @BypassMfa
    fun getEnabledProvidersForUser(principal: Principal): ResultRO<*> {
        return wrapResult {
            return@wrapResult mfaService.getEnabledProviders(principal.getUserInfo())
        }
    }
}