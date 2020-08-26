package com.antelopesystem.authframework.settings

import com.antelopesystem.authframework.settings.model.SecuritySettings
import com.antelopesystem.authframework.settings.model.SecuritySettingsRO
import com.antelopesystem.crudframework.crud.handler.CrudHandler
import com.antelopesystem.crudframework.modelfilter.dsl.where
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class SecuritySettingsHandlerImpl(
        private val crudHandler: CrudHandler
) : SecuritySettingsHandler {
    // todo: cache
    override fun getSecuritySettings(objectType: String): SecuritySettingsRO {
        return crudHandler.showBy(where {
            "objectType" Equal objectType
        }, SecuritySettings::class.java, SecuritySettingsRO::class.java)
                .execute() ?: crudHandler.fill(SecuritySettings(objectType), SecuritySettingsRO::class.java)
    }

}