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
        @Id
        var userId: Long,

        @Id
        var userAgent: String? = null,

        @Id
        var ip: String? = null,

        @Id
        var fingerprint: String? = null,

        @Id
        var countryIso: String,

        @Enumerated
        var status: UserLoginDeviceStatus = UserLoginDeviceStatus.Unknown,

        @Column(updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        var firstSeen: Date = Date(),

        @Temporal(TemporalType.TIMESTAMP)
        var lastSeen: Date = Date()

) : Serializable {
    constructor(loginDTO: UserLoginDTO) : this(loginDTO.userId, loginDTO.userAgent, loginDTO.ip, loginDTO.fingerprint, loginDTO.countryIso, UserLoginDeviceStatus.Verified)
}

