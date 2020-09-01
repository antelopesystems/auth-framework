package com.antelopesystem.authframework.authentication.model

data class UserInfo(
        val entityId: Long,

        val entityType: String,

        val grants: List<GrantRO> = listOf(),

        val suspiciousness: Suspiciousness = Suspiciousness.Low
)

enum class Suspiciousness {
    Low, Medium, High
}

data class GrantRO(
        val name: String? = null
)
