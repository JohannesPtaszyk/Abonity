package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.SubscriptionId

data class OverviewState(
    val detailId: SubscriptionId? = null,
    val periodSubscriptions: List<SelectableSubscriptionWithPeriodPrice> = emptyList(),
    val periodPrices: List<Price> = emptyList(),
)
