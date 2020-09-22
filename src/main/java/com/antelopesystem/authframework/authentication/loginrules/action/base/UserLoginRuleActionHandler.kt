package com.antelopesystem.authframework.authentication.loginrules.action.base

import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMapKey
import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO
import com.antelopesystem.authframework.authentication.loginrules.UserLoginRuleActionType

interface UserLoginRuleActionHandler {

    @get:ComponentMapKey
    val actionType: UserLoginRuleActionType

    fun handle(loginDTO: UserLoginDTO)
}