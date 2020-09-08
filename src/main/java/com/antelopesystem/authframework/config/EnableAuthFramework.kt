package com.antelopesystem.authframework.config

import com.antelopesystem.crudframework.jpa.annotation.EnableJpaCrud
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Import(AuthFrameworkConfig::class, AuthFrameworkSecurityConfig::class)
@EnableJpaCrud
annotation class EnableAuthFramework