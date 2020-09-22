package com.antelopesystem.authframework.authentication.model

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType

data class UserInfo(
        val entityId: Long = 0,

        val entityType: String = "",

        val externalId: String? = null,

        val grants: List<GrantRO> = listOf(),

        val suspiciousness: Suspiciousness = Suspiciousness.Low
) {
    fun getEntityPair() = EntityPair(entityId, entityType)
}

data class EntityPair(val entityId: Long, val type: String)

enum class Suspiciousness {
    Low, Medium, High
}

data class GrantRO(
        val name: String? = null
)
