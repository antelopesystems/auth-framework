package com.antelopesystem.authframework.authentication.logindevice.model

import com.antelopesystem.authframework.authentication.logindevice.enums.UserLoginDeviceStatus
import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_login_device_rel")
@CachedBy("com.mycompany.cache.USER_LOGIN_DEVICE_CACHE")
data class UserLoginDeviceRel(
        @get:Id
        @get:Column(name = "user_id")
        var userId: Long,

        @get:Id
        @get:Column(name = "user_agent")
        var userAgent: String? = null,

        @get:Id
        @get:Column(name = "ip")
        var ip: String? = null,

        @get:Id
        @get:Column(name = "fingerprint")
        var fingerprint: String? = null,

        @get:Id
        @get:Column(name = "country_iso")
        var countryIso: String,

        @get:Enumerated
        @get:Column(name = "status", columnDefinition = "INT(11) DEFAULT 0")
        var status: UserLoginDeviceStatus = UserLoginDeviceStatus.Unknown,

        @get:Column(name = "first_seen", updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        var firstSeen: Date = Date(),

        @get:Column(name = "last_seen")
        @Temporal(TemporalType.TIMESTAMP)
        var lastSeen: Date = Date()

) : Serializable {
    constructor(loginDTO: UserLoginDTO) : this(loginDTO.userId, loginDTO.userAgent, loginDTO.ip, loginDTO.fingerprint, loginDTO.countryIso, UserLoginDeviceStatus.Verified)
}

