package com.antelopesystem.authframework.base

import com.antelopesystem.authframework.base.authentication.model.AuthenticatedEntity
import com.antelopesystem.authframework.base.model.GrantRO
import com.antelopesystem.authframework.base.model.UserInfo
import com.antelopesystem.authframework.auth.model.ObjectToken
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority


data class TokenDTO(val token: ObjectToken)

class TbdAuthentication(private val userInfo: UserInfo) : Authentication {

    override fun getName(): String {
        return userInfo.entityId.toString()
    }

    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return userInfo.grants.map { SimpleGrantedAuthority(it.name) }
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return userInfo
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
    }
}

class TbdAuthenticationProvider(
        private val crudHandler: CrudHandler
) : AuthenticationProvider {
    override fun supports(authentication: Class<*>): Boolean {
        return TokenDTO::class.java.isAssignableFrom(authentication)
    }

    override fun authenticate(authentication: Authentication): Authentication {
        authentication as TokenDTO
        val entity = crudHandler.showBy(where {
            "entityId" Equal authentication.token.objectId
            "entityType" Equal authentication.token.objectType
        }, AuthenticatedEntity::class.java)
                .execute() ?: throw BadCredentialsException("Access Denied")
        val userInfo = UserInfo(
                entity.id,
                entity.type,
                false,
                crudHandler.getROs(entity.grants, GrantRO::class.java)

        )
        return TbdAuthentication(userInfo)
    }
}