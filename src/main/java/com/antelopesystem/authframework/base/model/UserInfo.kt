package com.antelopesystem.authframework.base.model

import com.antelopesystem.authframework.base.TokenDTO

data class UserInfo(
        val entityId: Long,

        val entityType: String,

        var blocked: Boolean,

        val grants: List<GrantRO> = listOf(),

        val suspiciousness: Suspiciousness = Suspiciousness.Low
)

enum class Suspiciousness {
    Low, Medium, High
}

data class GrantRO(
        val name: String
)
