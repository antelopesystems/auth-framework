package com.antelopesystem.authframework.auth

import com.antelopesystem.crudframework.jpa.dao.AbstractBaseDao
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class AuthenticationDaoImpl : AbstractBaseDao(), AuthenticationDao {
    override fun deleteOldTokens(toCreationTime: Long) {
        currentSession.createQuery("UPDATE ObjectToken t set t.expired = true WHERE t.creationTime < :toCreationTime AND t.expired=false AND t.immutable = false")
                .setParameter("toCreationTime", Date(toCreationTime))
                .executeUpdate()
    }
}