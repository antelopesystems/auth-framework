package com.antelopesystem.authframework.util

import com.antelopesystem.authframework.settings.model.SecuritySettings
import com.google.gson.GsonBuilder
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class SecuritySettingsConverter : AttributeConverter<SecuritySettings, String> {
    override fun convertToDatabaseColumn(attribute: SecuritySettings?): String? {
        attribute ?: return null
        return try {
            gson.toJson(attribute)
        } catch(e: Exception) {
            log.error(e) { "Serialization failed" }
            throw e
        }

    }

    override fun convertToEntityAttribute(dbData: String?): SecuritySettings? {
        dbData ?: return null
        return try {
            gson.fromJson(dbData, SecuritySettings::class.java)
        } catch(e: Exception) {
            log.error(e) { "Deserialization failed" }
            null
        }
    }

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        private val log = SecuritySettingsConverter.logger()
    }
}