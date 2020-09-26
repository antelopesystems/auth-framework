package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.CustomAuthenticationEntryPoint
import com.antelopesystem.authframework.authentication.filter.AuthenticationTokenProcessingFilter
import com.antelopesystem.authframework.authentication.filter.MfaFilter
import com.antelopesystem.authframework.authentication.filter.PasswordExpiryFilter
import com.antelopesystem.authframework.token.TokenAuthenticationProvider
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.authframework.util.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.CompositeFilter

@Configuration
class AuthFrameworkSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    private lateinit var tokenHandler: TokenHandler

    @Autowired
    private lateinit var tokenAuthenticationProvder: TokenAuthenticationProvider

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(getCompositeSecurityFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(tokenAuthenticationProvder)
    }

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER, "authenticationManager"])
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun authenticationTokenProcessingFilter() = AuthenticationTokenProcessingFilter(tokenHandler, authenticationManagerBean())

    @Bean
    fun authenticationTokenProcessingFilterRegistration(): FilterRegistrationBean<AuthenticationTokenProcessingFilter> {
        val registration = FilterRegistrationBean<AuthenticationTokenProcessingFilter>()
        registration.filter = authenticationTokenProcessingFilter()
        registration.isEnabled = false
        return registration
    }

    @Bean
    fun passwordExpiryFilter() = PasswordExpiryFilter(tokenHandler)

    @Bean
    fun passwordExpiryFilterRegistration(): FilterRegistrationBean<PasswordExpiryFilter> {
        val registration = FilterRegistrationBean<PasswordExpiryFilter>()
        registration.filter = passwordExpiryFilter()
        registration.isEnabled = false
        return registration
    }

    @Bean
    fun mfaFilter() = MfaFilter(tokenHandler)

    @Bean
    fun mfaFilterRegistration(): FilterRegistrationBean<MfaFilter> {
        val registration = FilterRegistrationBean<MfaFilter>()
        registration.filter = mfaFilter()
        registration.isEnabled = false
        return registration
    }

    private fun getCompositeSecurityFilter() = CompositeFilter().apply {
        setFilters(mutableListOf(
                authenticationTokenProcessingFilter(),
                mfaFilter(),
                passwordExpiryFilter()
        ))
    }
}