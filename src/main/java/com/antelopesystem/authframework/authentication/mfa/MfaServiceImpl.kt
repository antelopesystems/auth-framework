package com.antelopesystem.authframework.authentication.mfa

import com.antelopesystem.authframework.authentication.mfa.method.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.method.base.MfaType
import com.antelopesystem.authframework.authentication.model.EntityMfaMethod
import com.antelopesystem.authframework.authentication.model.EntityPair
import com.antelopesystem.authframework.authentication.model.MethodRequestPayload
import com.antelopesystem.authframework.authentication.model.UserInfo
import com.antelopesystem.authframework.entity.EntityHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Service

@Service
class MfaServiceImpl(private val crudHandler: CrudHandler,
                     private val securitySettingsHandler: SecuritySettingsHandler,
                     private val entityHandler: EntityHandler,
                     private val tokenHandler: TokenHandler) : MfaService {

    private val setupMfas = mutableMapOf<EntityPair, EntityMfaMethod>()

    @ComponentMap
    private lateinit var mfaProviders: Map<MfaType, MfaProvider>

    override fun setup(mfaType: MfaType, payload: MethodRequestPayload, userInfo: UserInfo) {
        val provider = getMfaProvider(mfaType, userInfo.entityType)

        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val mfaMethod = provider.setup(payload, entity)
        setupMfas[userInfo.getEntityPair()] = mfaMethod
    }

    override fun activate(mfaType: MfaType, code: String, userInfo: UserInfo) {
        val existingMethod = getMfaMethod(userInfo.entityId, userInfo.entityType, mfaType)
        if (existingMethod != null) {
            error("[ $mfaType ] is already configured")
        }

        val pendingSetup = setupMfas[userInfo.getEntityPair()] ?: error("No setup in progress")
        if (pendingSetup.type != mfaType) {
            error("No setup in progress")
        }
        val provider = getMfaProvider(mfaType, userInfo.entityType)
        provider.validate(code, pendingSetup)
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        entity.mfaMethods.add(pendingSetup)
        crudHandler.update(entity).execute()
    }

    override fun deactivate(mfaType: MfaType, userInfo: UserInfo) {
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        crudHandler.delete(existingMethod.id, EntityMfaMethod::class.java).execute()
    }

    override fun issue(mfaType: MfaType, userInfo: UserInfo) {
        val methodHandler = getMfaProvider(mfaType, userInfo.entityType)
        val pendingSetup = setupMfas[userInfo.getEntityPair()]
        if(pendingSetup != null && pendingSetup.type == mfaType) {
            methodHandler.issue(pendingSetup)
            return
        }
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        methodHandler.issue(existingMethod)
    }

    override fun validateToCurrentToken(mfaType: MfaType, code: String, userInfo: UserInfo) {
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        val methodHandler = getMfaProvider(mfaType, userInfo.entityType)
        methodHandler.validate(code, existingMethod)
        val token = tokenHandler.getCurrentToken()
        token.mfaRequired = false
        crudHandler.update(token).execute()
    }

    override fun getAvailableProviders(userInfo: UserInfo): List<ProviderDTO> {
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val providers = mutableListOf<ProviderDTO>()
        val securitySettings = securitySettingsHandler.getSecuritySettings(userInfo.entityType)
        for (mfaProvider in mfaProviders.values) {
            if(mfaProvider.isSupportedForType(userInfo.entityType)) {
                providers.add(ProviderDTO(
                        mfaProvider.type,
                        entity.mfaMethods.any { it.type == mfaProvider.type }
                ))
            }
        }

        return providers
    }

    override fun getEnabledProviders(userInfo: UserInfo): List<MfaType> {
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        return entity.mfaMethods.map { it.type }
    }

    private fun getMfaMethod(entityId: Long, entityType: String, mfaType: MfaType): EntityMfaMethod? {
        val entity = entityHandler.getEntity(entityId, entityType)
        return entity.mfaMethods.find { it.type == mfaType }
    }

    private fun getMfaMethodOrThrow(entityId: Long, entityType: String, mfaType: MfaType): EntityMfaMethod {
        val entity = entityHandler.getEntity(entityId, entityType)
        return getMfaMethod(entityId, entityType, mfaType) ?: error("No configuration found for type [ $mfaType ]")
    }

    private fun getMfaProvider(mfaType: MfaType, entityType: String): MfaProvider {
        val provider = mfaProviders[mfaType] ?: error("No provider found")
        if (!provider.isSupportedForType(entityType)) {
            error("Provider [ ${provider.type} ] is not supported")
        }
        return provider
    }
}

data class ProviderDTO(val mfaType: MfaType, val enabled: Boolean)