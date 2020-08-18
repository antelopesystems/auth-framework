package com.antelopesystem.authframework.auth

import com.antelopesystem.crudframework.jpa.dao.BaseDao


interface AuthenticationDao : BaseDao {
    fun deleteOldTokens(toCreationTime: Long)
}