package com.antelopesystem.authframework.geo

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnMissingBean(GeoIpResolver::class)
@Component
class DefaultGeoIpResolver : GeoIpResolver {
    override fun getCountryIso(ip: String): String = GeoIpResolver.DEFAULT_COUNTRY_ISO
}