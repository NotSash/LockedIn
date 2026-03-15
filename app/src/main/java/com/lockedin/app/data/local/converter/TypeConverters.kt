package com.lockedin.app.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room type converters for JSON-encoded fields.
 *
 * Sensitive values (e.g. CustomField.value) must already be encrypted
 * before being placed into these structures.
 */

@Serializable
data class CharacterTypes(
    val upper: Boolean,
    val lower: Boolean,
    val num: Boolean,
    val sym: Boolean
)

@Serializable
data class CustomField(
    val label: String,
    val value: String // encrypted value
)

object LockedInTypeConverters {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @TypeConverter
    @JvmStatic
    fun fromTagsList(tags: List<String>?): String? =
        tags?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toTagsList(jsonStr: String?): List<String>? =
        jsonStr?.let { json.decodeFromString(it) }

    @TypeConverter
    @JvmStatic
    fun fromCharacterTypes(types: CharacterTypes?): String? =
        types?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toCharacterTypes(jsonStr: String?): CharacterTypes? =
        jsonStr?.let { json.decodeFromString(it) }

    @TypeConverter
    @JvmStatic
    fun fromCustomFieldsList(list: List<CustomField>?): String? =
        list?.let { json.encodeToString(it) }

    @TypeConverter
    @JvmStatic
    fun toCustomFieldsList(jsonStr: String?): List<CustomField>? =
        jsonStr?.let { json.decodeFromString(it) }
}

