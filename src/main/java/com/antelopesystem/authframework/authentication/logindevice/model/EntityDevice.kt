package com.antelopesystem.authframework.authentication.logindevice.model

import com.antelopesystem.authframework.authentication.logindevice.enums.DeviceStatus
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.util.hashSHA256
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "entity_device")
//@CachedBy()
data class EntityDevice(
        var entityId: Long,

        var userAgent: String? = null,

        var ip: String? = null,

        var fingerprint: String? = null,

        var countryIso: String,

        @Enumerated
        var status: DeviceStatus = DeviceStatus.Unknown,

        @Column(updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        var firstSeen: Date = Date(),

        @Temporal(TemporalType.TIMESTAMP)
        var lastSeen: Date = Date(),

        @Id
        var hash: String = hashSHA256(entityId.toString() + userAgent.toString() + ip.toString() + fingerprint.toString() + countryIso)

) : Serializable {
    constructor(entityId: Long, deviceInfo: DeviceInfo) : this(entityId, deviceInfo.userAgent, deviceInfo.ip, deviceInfo.fingerprint, deviceInfo.countryIso, DeviceStatus.Verified)
}

