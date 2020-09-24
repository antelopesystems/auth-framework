package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.enums.DeviceStatus
import com.antelopesystem.authframework.authentication.logindevice.model.EntityDevice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class EntityDeviceHandlerImpl(
        private val entityDeviceDao: EntityDeviceDao
) : EntityDeviceHandler {
    @Transactional(readOnly = false)
    @Caching(evict = [
        CacheEvict(value = [CACHE_NAME], key = KNOWN_IPS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_COUNTRY_ISOS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_USER_AGENTS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_FINGERPRINTS_CACHE_KEY)
    ])
    override fun invalidateEntityDevicesAfterDate(entityId: Long, date: Date) {
        val loginDevices = entityDeviceDao.getValidDevicesAfterDate(entityId, date)

        for (loginDevice in loginDevices) {
            loginDevice.status = DeviceStatus.Invalid
            entityDeviceDao.saveOrUpdate(loginDevice)
        }
    }

    @Transactional(readOnly = false)
    @Caching(evict = [
        CacheEvict(value = [CACHE_NAME], key = "#device.entityId + '_known_ips'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.entityId + '_known_country_isos'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.entityId + '_known_user_agents'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.entityId + '_known_fingerprints'")
    ])
    override fun createOrUpdateDevice(device: EntityDevice): EntityDevice {
        return entityDeviceDao.saveOrUpdate(device)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_IPS_CACHE_KEY)
    override fun getEntityKnownIps(entityId: Long): List<String> {
        return entityDeviceDao.getEntityKnownIps(entityId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_COUNTRY_ISOS_CACHE_KEY)
    override fun getEntityKnownCountryIsos(entityId: Long): List<String> {
        return entityDeviceDao.getEntityKnownCountryIsos(entityId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_USER_AGENTS_CACHE_KEY)
    override fun getEntityKnownUserAgents(entityId: Long): List<String> {
        return entityDeviceDao.getEntityKnownUserAgents(entityId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key = KNOWN_FINGERPRINTS_CACHE_KEY)
    override fun getEntityKnownFingerprints(entityId: Long): List<String> {
        return entityDeviceDao.getEntityKnownFingerprints(entityId)
    }

    companion object {
        const val CACHE_NAME = "com.mycompany.cache.USER_LOGIN_DEVICE_CACHE"
        const val KNOWN_IPS_CACHE_KEY = "#entityId + '_known_ips'"
        const val KNOWN_COUNTRY_ISOS_CACHE_KEY = "#entityId + '_known_country_isos'"
        const val KNOWN_USER_AGENTS_CACHE_KEY = "#entityId + '_known_user_agents'"
        const val KNOWN_FINGERPRINTS_CACHE_KEY = "#entityId + '_known_fingerprints'"
    }
}