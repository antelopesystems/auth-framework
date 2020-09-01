package com.antelopesystem.authframework.config

import com.antelopesystem.authframework.authentication.CustomAuthenticationEntryPoint
import com.antelopesystem.authframework.authentication.filter.AuthenticationTokenProcessingFilter
import com.antelopesystem.authframework.token.TokenAuthenticationProvider
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
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

@Configuration
class AuthFrameworkSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var crudHandler: CrudHandler

    @Autowired
    private lateinit var tokenHandler: TokenHandler

    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(authenticationTokenProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(tokenAuthenticationProvder())
    }

    @Bean
    fun tokenAuthenticationProvder() = TokenAuthenticationProvider(crudHandler)

    /* Beans */
    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER, "authenticationManager"])
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    /* Validates the operator is not blocked */
    @Bean
    fun authenticationTokenProcessingFilter() = AuthenticationTokenProcessingFilter("User", tokenHandler, authenticationManagerBean())

    @Bean
    fun authenticationTokenProcessingFilterRegistration(): FilterRegistrationBean<AuthenticationTokenProcessingFilter> {
        val registration = FilterRegistrationBean<AuthenticationTokenProcessingFilter>()
        registration.filter = authenticationTokenProcessingFilter()
        registration.isEnabled = false
        return registration
    }
}