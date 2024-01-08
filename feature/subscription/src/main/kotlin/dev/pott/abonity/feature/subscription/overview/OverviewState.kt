package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.collections.immutable.ImmutableList

sealed interface OverviewState {
    data object Loading : OverviewState

    data class Loaded(
        val subscriptions: ImmutableList<SubscriptionWithPeriodInfo>,
        val filter: SubscriptionFilter,
        val detailId: SubscriptionId? = null,
    ) : OverviewState
}
