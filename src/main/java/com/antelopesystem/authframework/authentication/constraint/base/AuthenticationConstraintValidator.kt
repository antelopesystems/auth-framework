package com.antelopesystem.authframework.authentication.constraint.base

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.token.model.ObjectToken

interface AuthenticationConstraintValidator {
    fun validate(entity: AuthenticatedEntity, token: ObjectToken)
}