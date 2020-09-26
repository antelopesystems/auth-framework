package com.antelopesystem.authframework.authentication.constraint

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.constraint.base.AuthenticationConstraintValidator
import com.antelopesystem.authframework.authentication.model.Entity
import com.antelopesystem.authframework.token.model.Token
import org.springframework.stereotype.Component

@Component
class ActiveConstraintValidator : AuthenticationConstraintValidator {
    override fun validate(entity: Entity, token: Token) {
        val active = entity.active
        if(!active) {
            throw AccessDeniedException("Entity is not active")
        }
    }
}