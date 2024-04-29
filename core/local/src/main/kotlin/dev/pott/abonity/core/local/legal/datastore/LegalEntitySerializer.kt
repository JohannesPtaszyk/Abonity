package dev.pott.abonity.core.local.legal.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dev.pott.abonity.core.local.legal.datastore.entities.LegalEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class LegalEntitySerializer : Serializer<LegalEntity> {

    override val defaultValue: LegalEntity = LegalEntity()

    override suspend fun readFrom(input: InputStream): LegalEntity {
        try {
            return Json.decodeFromString(input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Legal Settings", serialization)
        }
    }

    override suspend fun writeTo(t: LegalEntity, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}
