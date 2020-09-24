package com.antelopesystem.authframework.authentication.rules

enum class DeviceAnomalyType(val points: Int) {
    UNKNOWN_IP(5), UNKNOWN_COUNTRY(20), UNKNOWN_USER_AGENT(10), UNKNOWN_FINGERPRINT(20)
}