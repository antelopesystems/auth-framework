package com.antelopesystem.authframework.authentication.constraint.base

import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.token.model.AuthToken

interface AuthenticationConstraintValidator {
    fun validate(entity: Entity, authToken: AuthToken)
}