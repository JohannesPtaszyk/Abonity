package dev.pott.abonity.feature.subscription.overview

data class SubscriptionOverviewState(
    val subscriptions: List<SubscriptionOverviewItem> = emptyList(),
)
