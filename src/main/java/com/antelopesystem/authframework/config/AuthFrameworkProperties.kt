package com.antelopesystem.authframework.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

private const val ROOT_PREFIX = "auth-framework"

@Configuration
@ConfigurationProperties(ROOT_PREFIX)
class AuthFrameworkProperties

@Configuration(ROOT_PREFIX + ".maxmind")
class MaxmindProperties {
    /**
     * Path to the .mmdb file in the classpath
     */
    var dbPath: String? = null
}