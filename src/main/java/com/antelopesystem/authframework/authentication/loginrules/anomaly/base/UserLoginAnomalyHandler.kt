package com.antelopesystem.authframework.authentication.loginrules.anomaly.base

import com.antelopesystem.authframework.authentication.loginrules.UserLoginAnomalyType
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey

interface UserLoginAnomalyHandler {

    @get:ComponentMapKey
    val anomalyType: UserLoginAnomalyType

    fun handle(loginDTO: UserLoginDTO): Boolean
}