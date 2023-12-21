package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class OverviewState(
    val detailId: SubscriptionId? = null,
    val periodSubscriptions: ImmutableList<SubscriptionWithPeriodInfo> = persistentListOf(),
    val periodPrices: ImmutableList<Price> = persistentListOf(),
)
