package com.antelopesystem.authframework.authentication.mfa

import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaProvider
import com.antelopesystem.authframework.authentication.mfa.provider.base.MfaType
import com.antelopesystem.authframework.authentication.model.*
import com.antelopesystem.authframework.entity.EntityHandler
import com.antelopesystem.authframework.settings.SecuritySettingsHandler
import com.antelopesystem.authframework.token.TokenHandler
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.utils.component.componentmap.annotation.ComponentMap
import org.springframework.stereotype.Service

@Service
class MfaServiceImpl(private val crudHandler: CrudHandler,
                     private val entityHandler: EntityHandler,
                     private val tokenHandler: TokenHandler,
                     private val securitySettingsHandler: SecuritySettingsHandler) : MfaService {

    private val setupMfas = mutableMapOf<MfaTrio, CustomParamsDTO>()

    @ComponentMap
    private lateinit var mfaProviders: Map<MfaType, MfaProvider>

    override fun setup(mfaType: MfaType, payload: MethodRequestPayload, userInfo: UserInfo): CustomParamsDTO {
        val provider = getMfaProvider(mfaType, userInfo.entityType)

        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val params = provider.setup(payload, entity)
        setupMfas[MfaTrio(entity.id, entity.type, mfaType)] = params
        return params
    }

    override fun activate(mfaType: MfaType, code: String, userInfo: UserInfo) {
        val existingMethod = getMfaMethod(userInfo.entityId, userInfo.entityType, mfaType)
        if (existingMethod != null) {
            error("[ $mfaType ] is already configured")
        }

        val pendingParams = setupMfas[MfaTrio(userInfo.entityId, userInfo.entityType, mfaType)] ?: error("No setup in progress")
        val provider = getMfaProvider(mfaType, userInfo.entityType)
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        provider.validate(code, entity, pendingParams)
        entity.mfaMethods.add(EntityMfaMethod(
                entity,
                mfaType,
                pendingParams
        ))
        crudHandler.update(entity).execute()
    }

    override fun deactivate(mfaType: MfaType, userInfo: UserInfo) {
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        crudHandler.delete(existingMethod.id, EntityMfaMethod::class.java).execute()
    }

    override fun issue(mfaType: MfaType, userInfo: UserInfo) {
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val methodHandler = getMfaProvider(mfaType, userInfo.entityType)
        val pendingParams = setupMfas[MfaTrio(userInfo.entityId, userInfo.entityType, mfaType)]
        if(pendingParams != null) {
            methodHandler.issue(entity, pendingParams)
            return
        }
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        methodHandler.issue(entity, crudHandler.getRO(existingMethod, CustomParamsDTO::class.java))
    }

    override fun validateToCurrentToken(mfaType: MfaType, code: String, userInfo: UserInfo) {
        val existingMethod = getMfaMethodOrThrow(userInfo.entityId, userInfo.entityType, mfaType)
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val methodHandler = getMfaProvider(mfaType, userInfo.entityType)
        methodHandler.validate(code, entity, crudHandler.getRO(existingMethod, CustomParamsDTO::class.java))
        val token = tokenHandler.getCurrentToken()
        token.mfaRequired = false
        crudHandler.update(token).execute()
    }

    override fun getAvailableProviders(userInfo: UserInfo): List<ProviderDTO> {
        val entity = entityHandler.getEntity(userInfo.entityId, userInfo.entityType)
        val securitySettings = securitySettingsHandler.getSecuritySettings(entity.type)
        val providers = mutableListOf<ProviderDTO>()
        for (mfaProvider in mfaProviders.values) {
            if(securitySettings.allowedMfaTypes.contains(mfaProvider.type)) {
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
        val securitySettings = securitySettingsHandler.getSecuritySettings(entityType)
        if (!securitySettings.allowedMfaTypes.contains(provider.type)) {
            error("Provider [ ${provider.type} ] is not supported")
        }
        return provider
    }
}

data class ProviderDTO(val mfaType: MfaType, val enabled: Boolean)

data class MfaTrio(val entityId: Long, val entityType: String, val mfaType: MfaType)