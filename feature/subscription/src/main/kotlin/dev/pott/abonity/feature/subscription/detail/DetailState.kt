package dev.pott.abonity.feature.subscription.detail

import dev.pott.abonity.core.entity.Subscription
import kotlinx.datetime.LocalDate

data class DetailState(
    val subscription: Subscription? = null,
    val nextPayment: LocalDate? = null,
)
