package com.antelopesystem.authframework.settings.model

import com.antelopesystem.authframework.util.SecuritySettingsConverter
import com.antelopesystem.crudframework.jpa.model.BaseJpaUpdatableEntity
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table
data class Settings(
        @get:Column(unique = true)
        var entityType: String,
        @get:Convert(converter = SecuritySettingsConverter::class)
        @get:Column(columnDefinition = "TEXT")
        var security: SecuritySettings = SecuritySettings()
): BaseJpaUpdatableEntity() {

}