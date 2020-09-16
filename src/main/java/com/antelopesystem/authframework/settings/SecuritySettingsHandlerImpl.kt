package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettings
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where

class SecuritySettingsHandlerImpl(
        private val crudHandler: CrudHandler
) : SecuritySettingsHandler {
    // todo: cache
    override fun getSecuritySettings(entityType: String): SecuritySettings {
        return crudHandler.showBy(where {
            "objectType" Equal entityType
        }, SecuritySettings::class.java)
                .execute() ?: SecuritySettings(entityType)
    }

}