package com.antelopesystem.authframework.authentication.logindevice.model

import com.antelopesystem.authframework.authentication.logindevice.enums.UserLoginDeviceStatus
import com.antelopesystem.crudframework.crud.annotation.CachedBy
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.util.hashSHA256
import com.antelopesystem.crudframework.crud.annotation.Immutable
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_login_device_rel")
@CachedBy("com.mycompany.cache.USER_LOGIN_DEVICE_CACHE")
data class UserLoginDeviceRel(
        var userId: Long,

        var userAgent: String? = null,

        var ip: String? = null,

        var fingerprint: String? = null,

        var countryIso: String,

        @Enumerated
        var status: UserLoginDeviceStatus = UserLoginDeviceStatus.Unknown,

        @Column(updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        var firstSeen: Date = Date(),

        @Temporal(TemporalType.TIMESTAMP)
        var lastSeen: Date = Date(),

        @Id
        var hash: String = hashSHA256(userId.toString() + userAgent.toString() + ip.toString() + fingerprint.toString() + countryIso)

) : Serializable {
    constructor(loginDTO: UserLoginDTO) : this(loginDTO.userId, loginDTO.userAgent, loginDTO.ip, loginDTO.fingerprint, loginDTO.countryIso, UserLoginDeviceStatus.Verified)
}

