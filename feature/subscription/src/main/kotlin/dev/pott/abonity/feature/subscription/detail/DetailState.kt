package dev.pott.abonity.feature.subscription.detail

import androidx.compose.runtime.Immutable
import dev.pott.abonity.core.entity.subscription.Subscription
import kotlinx.datetime.LocalDate

@Immutable
data class DetailState(
    val subscription: Subscription? = null,
    val nextPayment: LocalDate? = null,
    val close: Boolean = false,
)
