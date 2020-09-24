package com.antelopesystem.authframework.geo

import com.maxmind.geoip2.DatabaseReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.net.InetAddress
import javax.annotation.PostConstruct

@ConditionalOnProperty("auth-framework.maxmind.db-path")
@ConditionalOnClass(DatabaseReader::class)
@Component
class MaxmindGeoIpResolver(
        @Value("\${auth-framework.maxmind.db-path}") private val dbPath: String
) : GeoIpResolver {
    private lateinit var databaseReader: DatabaseReader

    @PostConstruct
    private fun init() {
        val resource: Resource = ClassPathResource(dbPath)
        val inputStream = resource.inputStream
        databaseReader = DatabaseReader.Builder(inputStream).build()
    }
    override fun getCountryIso(ip: String): String {
        return try {
            val ipAddress = InetAddress.getByName(ip)
            val response = databaseReader.country(ipAddress)
            return response.country.isoCode ?: GeoIpResolver.DEFAULT_COUNTRY_ISO
        } catch (e: Exception) {
            return GeoIpResolver.DEFAULT_COUNTRY_ISO
        }
    }
}