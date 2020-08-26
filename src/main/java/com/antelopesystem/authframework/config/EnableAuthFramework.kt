package com.antelopesystem.authframework.config

import com.antelopesystem.crudframework.utils.component.startup.annotation.EnablePostStartup
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.EnableComponentMap
import com.antelopesystem.crudframework.crud.configuration.CrudFrameworkConfiguration
import com.antelopesystem.crudframework.jpa.annotation.EnableJpaCrud
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Import(AuthFrameworkConfig::class)
@EnableJpaCrud
annotation class EnableAuthFramework