package com.antelopesystem.authframework.authentication.constraint

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.token.model.ObjectToken
import org.springframework.stereotype.Component

class ActiveConstraintValidator : AuthenticationConstraintValidator {
    override fun validate(entity: AuthenticatedEntity, token: ObjectToken) {
        val active = entity.active
        if(!active) {
            throw AccessDeniedException("Entity is not active")
        }
    }
}