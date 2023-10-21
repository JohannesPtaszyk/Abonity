package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.SubscriptionId

data class OverviewState(
    val detailId: SubscriptionId? = null,
    val subscriptions: List<SubscriptionItem> = emptyList(),
)
