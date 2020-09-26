package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.util.JsonObjectAttributeConverter
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatebleEntity
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table
data class Settings(
        @get:Column(unique = true)
        var entityType: String,
        @get:Convert(converter = JsonObjectAttributeConverter::class)
        @get:Column(columnDefinition = "TEXT")
        var security: SecuritySettings = SecuritySettings()
): BaseJpaUpdatebleEntity() {

}