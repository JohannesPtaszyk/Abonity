package dev.pott.abonity.core.entity.subscription

import kotlinx.datetime.LocalDate

data class UpcomingSubscriptions(
    val subscriptions: Map<LocalDate, List<SubscriptionWithPeriodInfo>>,
    val hasAnySubscriptions: Boolean,
    val period: PaymentPeriod,
)
