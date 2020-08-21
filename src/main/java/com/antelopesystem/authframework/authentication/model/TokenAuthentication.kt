package com.antelopesystem.authframework.authentication.model

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

class TokenAuthentication(private val userInfo: UserInfo) : Authentication {

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