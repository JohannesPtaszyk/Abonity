package dev.pott.abonity.core.local.legal.datastore.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegalEntity(
    @SerialName("consents")
    val consents: List<ConsentEntity> = emptyList(),
)
