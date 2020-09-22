package com.antelopesystem.authframework.authentication.loginrules

import com.antelopesystem.authframework.authentication.loginrules.dto.UserLoginDTO

interface UserLoginValidator {
    fun validateLogin(loginDTO: UserLoginDTO): Int
}