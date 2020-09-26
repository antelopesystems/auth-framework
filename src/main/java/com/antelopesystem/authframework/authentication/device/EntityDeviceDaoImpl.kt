package com.antelopesystem.authframework.authentication.device

import com.antelopesystem.authframework.authentication.device.enums.DeviceStatus
import com.antelopesystem.authframework.authentication.device.model.EntityDevice
import com.antelopesystem.crudframework.jpa.dao.AbstractBaseDao
import org.hibernate.criterion.Restrictions
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class EntityDeviceDaoImpl : EntityDeviceDao, AbstractBaseDao() {
    override fun getEntityKnownIps(entityId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.ip FROM EntityDevice l WHERE l.entityId = :entityId AND l.status = :status AND l.ip IS NOT NULL")
                .setParameter("status", DeviceStatus.Verified)
                .setParameter("entityId", entityId)
                .list() as List<String>
    }

    override fun getEntityKnownCountryIsos(entityId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.countryIso FROM EntityDevice l WHERE l.entityId = :entityId AND l.status = :status AND l.countryIso IS NOT NULL")
                .setParameter("status", DeviceStatus.Verified)
                .setParameter("entityId", entityId)
                .list() as List<String>
    }

    override fun getEntityKnownUserAgents(entityId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.userAgent FROM EntityDevice l WHERE l.entityId = :entityId AND l.status = :status AND l.userAgent IS NOT NULL")
                .setParameter("status", DeviceStatus.Verified)
                .setParameter("entityId", entityId)
                .list() as List<String>
    }

    override fun getEntityKnownFingerprints(entityId: Long): List<String> {
        return currentSession
                .createQuery("SELECT DISTINCT l.fingerprint FROM EntityDevice l WHERE l.entityId = :entityId AND l.status = :status AND l.fingerprint IS NOT NULL")
                .setParameter("status", DeviceStatus.Verified)
                .setParameter("entityId", entityId)
                .list() as List<String>
    }

    override fun getValidDevicesAfterDate(entityId: Long, date: Date): List<EntityDevice> {
        return currentSession
                .createCriteria(EntityDevice::class.java)
                .add(Restrictions.ge("firstSeen", date))
                .add(Restrictions.ne("status", DeviceStatus.Invalid))
                .add(Restrictions.eq("entityId", entityId))
                .list() as List<EntityDevice>
    }
}