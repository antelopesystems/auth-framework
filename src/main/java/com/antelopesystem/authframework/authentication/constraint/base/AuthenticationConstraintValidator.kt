package com.antelopesystem.authframework.authentication.constraint.base

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.token.model.Token

interface AuthenticationConstraintValidator {
    fun validate(entity: AuthenticatedEntity, token: Token)
}