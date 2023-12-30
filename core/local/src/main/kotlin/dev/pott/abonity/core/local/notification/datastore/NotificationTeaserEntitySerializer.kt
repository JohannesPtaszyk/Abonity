package dev.pott.abonity.core.local.notification.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dev.pott.abonity.core.local.notification.datastore.entities.NotificationTeaserEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class NotificationTeaserEntitySerializer : Serializer<NotificationTeaserEntity> {

    override val defaultValue: NotificationTeaserEntity = NotificationTeaserEntity()

    override suspend fun readFrom(input: InputStream): NotificationTeaserEntity {
        try {
            return Json.decodeFromString(input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: NotificationTeaserEntity, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}
