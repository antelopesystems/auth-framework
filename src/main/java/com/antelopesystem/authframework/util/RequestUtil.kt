package com.antelopesystem.authframework.util

import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.getUserAgent(): String? {
    return this.getHeader("User-Agent")
}

fun HttpServletRequest.getFingerprint(): String? {
    return this.getHeader("X-FINGERPRINT")
}

// Todo: Ipv6 support
fun HttpServletRequest.getIpAddress(): String {
    return try {
        var trustXForwardedForRaw = System.getenv("TRUST_X_FORWARDED_FOR")
        if (trustXForwardedForRaw == null) {
            trustXForwardedForRaw = System.getProperty("TRUST_X_FORWARDED_FOR")
        }
        val trustXForwardedFor = trustXForwardedForRaw?.toBoolean() ?: false
        var ipAddress: String? = null
        if (trustXForwardedFor) {
            var xForwardedForHeaderValue = this.getHeader("x-forwarded-for")
            if (xForwardedForHeaderValue == null) {
                xForwardedForHeaderValue = this.getHeader("X_FORWARDED_FOR")
            }
            if (xForwardedForHeaderValue != null) {
                try {
                    val xForwardedForArray = xForwardedForHeaderValue.split(",".toRegex()).toTypedArray()
                    ipAddress = xForwardedForArray[xForwardedForArray.size - 1].trim()
                } catch (e: Exception) {
                }
            }
        }
        if (ipAddress == null) {
            return this.remoteAddr
        }
        ipAddress
    } catch (e: Exception) {
        this.remoteAddr
    }
}