package dev.pott.abonity.core.entity.subscription

data class UpcomingSubscriptions(
    val subscriptions: List<SubscriptionWithPeriodInfo>,
    val hasAnySubscriptions: Boolean,
    val period: PaymentPeriod,
)
