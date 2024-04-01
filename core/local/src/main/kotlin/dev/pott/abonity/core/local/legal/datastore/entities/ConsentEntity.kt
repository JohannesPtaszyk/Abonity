package dev.pott.abonity.core.local.legal.datastore.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsentEntity(
    @SerialName("serviceId")
    val serviceId: ConsentServiceIdEntity,
    @SerialName("consentGrant")
    val consentGrant: ConsentGrantEntity,
)
