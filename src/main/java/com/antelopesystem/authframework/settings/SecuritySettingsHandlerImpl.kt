package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettings
import com.antelopesystem.authframework.settings.model.Settings
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class SecuritySettingsHandlerImpl(
        private val crudHandler: CrudHandler
) : SecuritySettingsHandler {
    // todo: cache
    override fun getSecuritySettings(entityType: String): SecuritySettings {
        return getSettings(entityType).security
    }

    override fun getSettings(entityType: String): Settings {
        return crudHandler.showBy(where {
            "entityType" Equal entityType
        }, Settings::class.java)
                .execute() ?: crudHandler.create(Settings(entityType)).execute()
    }
}