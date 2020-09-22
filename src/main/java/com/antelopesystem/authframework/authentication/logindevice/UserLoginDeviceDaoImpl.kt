package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.enums.UserLoginDeviceStatus
import com.antelopesystem.authframework.authentication.logindevice.model.UserLoginDeviceRel
import com.antelopesystem.crudframework.jpa.dao.AbstractBaseDao
import org.hibernate.criterion.Restrictions
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserLoginDeviceDaoImpl : UserLoginDeviceDao, AbstractBaseDao() {
    override fun getUserKnownIps(userId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.ip FROM UserLoginDeviceRel l WHERE l.userId = :userId AND l.status = :status AND l.ip IS NOT NULL")
                .setParameter("status", UserLoginDeviceStatus.Verified)
                .setParameter("userId", userId)
                .list() as List<String>
    }

    override fun getUserKnownCountryIsos(userId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.countryIso FROM UserLoginDeviceRel l WHERE l.userId = :userId AND l.status = :status AND l.countryIso IS NOT NULL")
                .setParameter("status", UserLoginDeviceStatus.Verified)
                .setParameter("userId", userId)
                .list() as List<String>
    }

    override fun getUserKnownUserAgents(userId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.userAgent FROM UserLoginDeviceRel l WHERE l.userId = :userId AND l.status = :status AND l.userAgent IS NOT NULL")
                .setParameter("status", UserLoginDeviceStatus.Verified)
                .setParameter("userId", userId)
                .list() as List<String>
    }

    override fun getUserKnownFingerprints(userId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.fingerprint FROM UserLoginDeviceRel l WHERE l.userId = :userId AND l.status = :status AND l.fingerprint IS NOT NULL")
                .setParameter("status", UserLoginDeviceStatus.Verified)
                .setParameter("userId", userId)
                .list() as List<String>
    }

    override fun getValidDevicesAfterDate(userId: Long, date: Date): List<UserLoginDeviceRel> {
        return currentSession
                .createCriteria(UserLoginDeviceRel::class.java)
                .add(Restrictions.ge("firstSeen", date))
                .add(Restrictions.ne("status", UserLoginDeviceStatus.Invalid))
                .add(Restrictions.eq("userId", userId))
                .list() as List<UserLoginDeviceRel>
    }
}