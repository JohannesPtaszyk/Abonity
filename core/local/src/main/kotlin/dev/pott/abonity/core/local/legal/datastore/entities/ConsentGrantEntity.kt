package dev.pott.abonity.core.local.legal.datastore.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ConsentGrantEntity {
    @SerialName("granted")
    GRANTED,

    @SerialName("denied")
    DENIED,

    @SerialName("unknown")
    UNKNOWN,
}
