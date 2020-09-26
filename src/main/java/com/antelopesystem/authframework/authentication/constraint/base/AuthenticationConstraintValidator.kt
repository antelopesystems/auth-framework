package com.antelopesystem.authframework.authentication.constraint.base

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.token.model.Token

interface AuthenticationConstraintValidator {
    fun validate(entity: Entity, token: Token)
}