package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class OverviewState(
    val detailId: SubscriptionId? = null,
    val periodSubscriptions: List<SelectableSubscriptionWithPeriodPrice> = emptyList(),
    val periodPrices: ImmutableList<Price> = persistentListOf(),
)
