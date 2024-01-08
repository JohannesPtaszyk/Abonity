package dev.pott.abonity.core.entity.subscription

data class SubscriptionsWithFilter(
    val filteredSubscriptions: List<SubscriptionWithPeriodInfo>,
    val filter: SubscriptionFilter,
)
