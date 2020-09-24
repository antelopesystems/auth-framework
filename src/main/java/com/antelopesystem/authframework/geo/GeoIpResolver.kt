package com.antelopesystem.authframework.geo

interface GeoIpResolver {
    fun getCountryIso(ip: String): String
    companion object {
        const val DEFAULT_COUNTRY_ISO = "XX"
    }
}