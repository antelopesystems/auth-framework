package com.antelopesystem.authframework.util

import com.antelopesystem.authframework.authentication.model.UserInfo
import com.antelopesystem.authframework.token.model.TokenAuthentication
import java.security.Principal

fun Principal.getUserInfo(): UserInfo {
    if(this !is TokenAuthentication) {
        error("Invalid principal")
    }
    return this.userInfo
}