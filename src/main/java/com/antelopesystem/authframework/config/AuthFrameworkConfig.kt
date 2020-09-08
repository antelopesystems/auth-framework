package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.*
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.nexmo.NexmoAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.authentication.method.nexmo.NexmoClientProvider
import com.antelopesystem.authframework.authentication.method.usernamepassword.UsernamePasswordAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifier
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifierImpl
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandlerImpl
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.TokenHandlerImpl
import com.antelopesystem.authframework.token.type.FingerprintedTimestampAuthenticationHandlerImpl
import com.antelopesystem.authframework.token.type.LegacyAuthenticationHandlerImpl
import com.antelopesystem.authframework.token.type.TimestampAuthenticationHandlerImpl
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

    @Autowired
    private lateinit var authenticationMethodHandlers: List<AuthenticationMethodHandler>

    @Bean
    fun securitySettingsHandler(): SecuritySettingsHandler = SecuritySettingsHandlerImpl(crudHandler)

    @Bean
    fun authenticationService(): AuthenticationService = AuthenticationServiceImpl(tokenHandler(), authenticationNotifier(), crudHandler, securitySettingsHandler(), authenticationMethodHandlers)

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

    @Bean
    fun legacyAuthenticationHandler() = LegacyAuthenticationHandlerImpl()

    @Bean
    fun fingerprintedTimestampAuthenticationHandler() = FingerprintedTimestampAuthenticationHandlerImpl()

    @Bean
    fun timestampAuthenticationHandler() = TimestampAuthenticationHandlerImpl()
}