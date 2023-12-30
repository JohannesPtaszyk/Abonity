package dev.pott.abonity.core.local.notification.datastore.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationTeaserEntity(
    @SerialName("lastClosedLocalDateTime")
    val lastClosedLocalDateTime: String? = null,
    @SerialName("timeZoneId")
    val timeZoneId: String? = null,
    @SerialName("shouldNotShowAgain")
    val shouldNotShowAgain: Boolean = false,
) {
    init {
        if (lastClosedLocalDateTime != null) {
            checkNotNull(timeZoneId) {
                "lastClosedLocalDateTime must be provided with timeZoneId"
            }
        }
    }
}
