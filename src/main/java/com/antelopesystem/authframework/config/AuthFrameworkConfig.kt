package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.AuthenticationService
import com.antelopesystem.authframework.authentication.AuthenticationServiceImpl
import com.antelopesystem.authframework.authentication.AuthenticationTypeHandler
import com.antelopesystem.authframework.authentication.nexmo.NexmoAuthenticationTypeHandlerImpl
import com.antelopesystem.authframework.authentication.usernamepassword.UsernamePasswordTypeHandlerImpl
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandlerImpl
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthFrameworkConfig(
        private val crudHandler: CrudHandler
) {

    @Bean
    fun securitySettingsHandler(): SecuritySettingsHandler = SecuritySettingsHandlerImpl(crudHandler)

    @Bean
    fun authenticationService(): AuthenticationService = AuthenticationServiceImpl()

    @Bean
    fun nexmoAuthenticationTypeHandler(): AuthenticationTypeHandler = NexmoAuthenticationTypeHandlerImpl(crudHandler, securitySettingsHandler())

    @Bean
    fun usernamePasswordAuthenticationTypeHandler(): AuthenticationTypeHandler = UsernamePasswordTypeHandlerImpl(crudHandler, securitySettingsHandler())


}