package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.authentication.AccessDeniedException
import com.antelopesystem.authframework.authentication.constraint.base.AuthenticationConstraintValidator
import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.GrantRO
import com.antelopesystem.authframework.token.model.TokenAuthentication
import com.antelopesystem.authframework.authentication.model.UserInfo
import com.antelopesystem.authframework.token.model.TokenAuthenticationRequest
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class TokenAuthenticationProvider(
        private val crudHandler: CrudHandler,
        @Autowired(required=false) private val constraintValidators: List<AuthenticationConstraintValidator> = emptyList()
) : AuthenticationProvider {
    override fun supports(authentication: Class<*>): Boolean {
        return TokenAuthenticationRequest::class.java.isAssignableFrom(authentication)
    }

    override fun authenticate(authentication: Authentication): Authentication {
        authentication as TokenAuthenticationRequest
        val entity = crudHandler.showBy(where {
            "id" Equal authentication.token.objectId
            "type" Equal authentication.token.objectType
        }, AuthenticatedEntity::class.java)
                .execute() ?: throw AccessDeniedException("Entity not found")
        for (constraintValidator in constraintValidators) {
            constraintValidator.validate(entity, authentication.token)
        }

        val userInfo = crudHandler.getRO(entity, UserInfo::class.java)
        return TokenAuthentication(userInfo)
    }
}