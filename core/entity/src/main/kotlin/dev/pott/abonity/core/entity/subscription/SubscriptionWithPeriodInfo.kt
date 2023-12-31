package dev.pott.abonity.core.entity.subscription

import kotlinx.datetime.LocalDate

data class SubscriptionWithPeriodInfo(
    val subscription: Subscription,
    val periodPrice: Price,
    val nextPaymentDate: LocalDate,
)
