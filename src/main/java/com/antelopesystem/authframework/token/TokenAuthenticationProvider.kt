package com.antelopesystem.authframework.token

import com.antelopesystem.authframework.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.authentication.model.GrantRO
import com.antelopesystem.authframework.token.model.TokenAuthentication
import com.antelopesystem.authframework.authentication.model.UserInfo
import com.antelopesystem.authframework.token.model.TokenAuthenticationRequest
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication


class TokenAuthenticationProvider(
        private val crudHandler: CrudHandler
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
                .execute() ?: throw BadCredentialsException("Access Denied")
        val userInfo = UserInfo(
                entity.id,
                entity.type,
                false,
                crudHandler.getROs(entity.grants, GrantRO::class.java)

        )
        return TokenAuthentication(userInfo)
    }
}