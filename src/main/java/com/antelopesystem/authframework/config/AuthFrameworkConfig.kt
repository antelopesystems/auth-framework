package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.*
import com.antelopesystem.authframework.authentication.listener.LoginListener
import com.antelopesystem.authframework.authentication.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.nexmo.NexmoAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.authentication.nexmo.NexmoClientProvider
import com.antelopesystem.authframework.authentication.usernamepassword.UsernamePasswordAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandlerImpl
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.TokenHandlerImpl
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthFrameworkConfig(
        private val crudHandler: CrudHandler
) {

    @Autowired(required = false)
    private var loginListeners: List<LoginListener> = listOf()

    @Autowired(required = false)
    private var registrationListeners: List<RegistrationListener> = listOf()

    @Bean
    fun securitySettingsHandler(): SecuritySettingsHandler = SecuritySettingsHandlerImpl(crudHandler)

    @Bean
    fun authenticationService(): AuthenticationService = AuthenticationServiceImpl(tokenHandler(), authenticationNotifier(), crudHandler, securitySettingsHandler())

    @Autowired
    fun nexmoClientProvider() = NexmoClientProvider(securitySettingsHandler())

    @Bean
    fun nexmoAuthenticationTypeHandler(): AuthenticationMethodHandler = NexmoAuthenticationMethodHandlerImpl(crudHandler, nexmoClientProvider(), securitySettingsHandler())

    @Bean
    fun usernamePasswordAuthenticationTypeHandler(): AuthenticationMethodHandler = UsernamePasswordAuthenticationMethodHandlerImpl(crudHandler)

    @Bean
    fun authenticationNotifier(): AuthenticationNotifier = AuthenticationNotifierImpl(loginListeners, registrationListeners)

    @Bean
    fun tokenHandler(): TokenHandler = TokenHandlerImpl()


}