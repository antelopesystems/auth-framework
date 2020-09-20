package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.*
import com.antelopesystem.authframework.authentication.method.authenticator.AuthenticatorMethodHandlerImpl
import com.antelopesystem.authframework.authentication.notifier.listener.LoginListener
import com.antelopesystem.authframework.authentication.notifier.listener.RegistrationListener
import com.antelopesystem.authframework.authentication.method.base.AuthenticationMethodHandler
import com.antelopesystem.authframework.authentication.method.nexmo.NexmoAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.integrations.NexmoClientProvider
import com.antelopesystem.authframework.authentication.method.usernamepassword.UsernamePasswordAuthenticationMethodHandlerImpl
import com.antelopesystem.authframework.authentication.mfa.MfaService
import com.antelopesystem.authframework.authentication.mfa.MfaServiceImpl
import com.antelopesystem.authframework.authentication.mfa.method.NexmoMfaProvider
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifier
import com.antelopesystem.authframework.authentication.notifier.AuthenticationNotifierImpl
import com.antelopesystem.authframework.authentication.notifier.listener.ForgotPasswordListener
import com.antelopesystem.authframework.entity.EntityHandler
import com.antelopesystem.authframework.entity.EntityHandlerImpl
import com.antelopesystem.authframework.integrations.AuthenticatorClientProvider
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandlerImpl
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.token.TokenHandlerImpl
import com.antelopesystem.authframework.token.type.FingerprintedTimestampAuthenticationHandlerImpl
import com.antelopesystem.authframework.token.type.LegacyAuthenticationHandlerImpl
import com.antelopesystem.authframework.token.type.PFTAuthenticationHandlerImpl
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

    @Autowired(required = false)
    private var forgotPasswordListeners: List<ForgotPasswordListener> = listOf()

    @Bean
    fun securitySettingsHandler(): SecuritySettingsHandler = SecuritySettingsHandlerImpl(crudHandler)

    @Bean
    fun authenticationService(): AuthenticationService = AuthenticationServiceImpl(tokenHandler(), authenticationNotifier(), crudHandler, securitySettingsHandler())

    @Autowired
    fun nexmoClientProvider() = NexmoClientProvider(securitySettingsHandler())

    @Autowired
    fun authenticatorClientProvider() = AuthenticatorClientProvider(securitySettingsHandler())

    @Bean
    fun nexmoAuthenticationTypeHandler(): AuthenticationMethodHandler = NexmoAuthenticationMethodHandlerImpl(crudHandler, nexmoClientProvider(), securitySettingsHandler())

    @Bean
    fun usernamePasswordAuthenticationTypeHandler(): AuthenticationMethodHandler = UsernamePasswordAuthenticationMethodHandlerImpl(crudHandler, securitySettingsHandler())

    @Bean
    fun authenticatorMethodHandler()  = AuthenticatorMethodHandlerImpl(crudHandler)

    @Bean
    fun authenticationNotifier(): AuthenticationNotifier = AuthenticationNotifierImpl(loginListeners, registrationListeners, forgotPasswordListeners)

    @Bean
    fun tokenHandler(): TokenHandler = TokenHandlerImpl()

    @Bean
    fun legacyAuthenticationHandler() = LegacyAuthenticationHandlerImpl()

    @Bean
    fun fingerprintedTimestampAuthenticationHandler() = FingerprintedTimestampAuthenticationHandlerImpl()

    @Bean
    fun timestampAuthenticationHandler() = TimestampAuthenticationHandlerImpl()

    @Bean
    fun entityHandler(): EntityHandler = EntityHandlerImpl(crudHandler)

    @Bean
    fun pftAuthenticationandler() = PFTAuthenticationHandlerImpl()

    @Bean
    fun nexmoMfaProvider() = NexmoMfaProvider(nexmoClientProvider(), securitySettingsHandler())

    @Bean
    fun mfaService() = MfaServiceImpl(crudHandler, securitySettingsHandler(), entityHandler(), tokenHandler())
}