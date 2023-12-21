package dev.pott.abonity.core.local.settings.datastore.entities

import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsEntity(
    @SerialName("theme")
    val theme: LocalTheme = LocalTheme.FOLLOW_SYSTEM,
    @SerialName("paymentPeriod")
    val period: LocalPaymentPeriod = LocalPaymentPeriod.MONTHS,
)
