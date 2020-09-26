package com.antelopesystem.authframework.util

import com.google.gson.GsonBuilder
import java.io.Serializable
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class JsonObjectAttributeConverter : AttributeConverter<Serializable, String> {
    override fun convertToDatabaseColumn(attribute: Serializable?): String? {
        attribute ?: return null
        return try {
            gson.toJson(attribute)
        } catch(e: Exception) {
            log.error(e) { "Serialization failed" }
            throw e
        }

    }

    override fun convertToEntityAttribute(dbData: String?): Serializable? {
        dbData ?: return null
        return try {
            gson.fromJson(dbData, Serializable::class.java)
        } catch(e: Exception) {
            log.error(e) { "Deserialization failed" }
            null
        }
    }

    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        private val log = JsonObjectAttributeConverter.logger()
    }
}