package com.antelopesystem.authframework.authentication.rules.action.base

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.authentication.rules.dto.DeviceInfo
import com.antelopesystem.authframework.authentication.rules.RuleActionType

interface RuleActionHandler {

    @get:ComponentMapKey
    val actionType: RuleActionType

    fun handle(entity: Entity, deviceInfo: DeviceInfo)
}