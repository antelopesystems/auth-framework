package com.antelopesystem.authframework.authentication.logindevice

import com.antelopesystem.authframework.authentication.logindevice.enums.UserLoginDeviceStatus
import com.antelopesystem.authframework.authentication.logindevice.model.UserLoginDeviceRel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class UserLoginDeviceHandlerImpl : UserLoginDeviceHandler {

    @Autowired
    private lateinit var userLoginDeviceDao: UserLoginDeviceDao

    @Transactional(readOnly = false)
    @Caching(evict = [
        CacheEvict(value = [CACHE_NAME], key = KNOWN_IPS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_COUNTRY_ISOS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_USER_AGENTS_CACHE_KEY),
        CacheEvict(value = [CACHE_NAME], key = KNOWN_FINGERPRINTS_CACHE_KEY)
    ])
    override fun invalidateUserDevicesAfterDate(userId: Long, date: Date) {
        val loginDevices = userLoginDeviceDao.getValidDevicesAfterDate(userId, date)

        for (loginDevice in loginDevices) {
            loginDevice.status = UserLoginDeviceStatus.Invalid
            userLoginDeviceDao.saveOrUpdate(loginDevice)
        }
    }

    @Transactional(readOnly = false)
    @Caching(evict = [
        CacheEvict(value = [CACHE_NAME], key = "#device.userId + '_known_ips'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.userId + '_known_country_isos'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.userId + '_known_user_agents'"),
        CacheEvict(value = [CACHE_NAME], key = "#device.userId + '_known_fingerprints'")
    ])
    override fun createOrUpdateDevice(device: UserLoginDeviceRel): UserLoginDeviceRel {
        return userLoginDeviceDao.saveOrUpdate(device)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_IPS_CACHE_KEY)
    override fun getUserKnownIps(userId: Long): List<String> {
        return userLoginDeviceDao.getUserKnownIps(userId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_COUNTRY_ISOS_CACHE_KEY)
    override fun getUserKnownCountryIsos(userId: Long): List<String> {
        return userLoginDeviceDao.getUserKnownCountryIsos(userId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key=KNOWN_USER_AGENTS_CACHE_KEY)
    override fun getUserKnownUserAgents(userId: Long): List<String> {
        return userLoginDeviceDao.getUserKnownUserAgents(userId)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = [CACHE_NAME], key = KNOWN_FINGERPRINTS_CACHE_KEY)
    override fun getUserKnownFingerprints(userId: Long): List<String> {
        return userLoginDeviceDao.getUserKnownFingerprints(userId)
    }

    companion object {
        const val CACHE_NAME = "com.mycompany.cache.USER_LOGIN_DEVICE_CACHE"
        const val KNOWN_IPS_CACHE_KEY = "#userId + '_known_ips'"
        const val KNOWN_COUNTRY_ISOS_CACHE_KEY = "#userId + '_known_country_isos'"
        const val KNOWN_USER_AGENTS_CACHE_KEY = "#userId + '_known_user_agents'"
        const val KNOWN_FINGERPRINTS_CACHE_KEY = "#userId + '_known_fingerprints'"
    }
}