package dev.pott.abonity.core.local.settings.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class SettingsEntitySerializer : Serializer<SettingsEntity> {

    override val defaultValue: SettingsEntity = SettingsEntity()

    override suspend fun readFrom(input: InputStream): SettingsEntity {
        try {
            return Json.decodeFromString(input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: SettingsEntity, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}
