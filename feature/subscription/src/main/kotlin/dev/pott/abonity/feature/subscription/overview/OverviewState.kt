package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilterState
import kotlinx.collections.immutable.ImmutableList

sealed interface OverviewState {
    data object Loading : OverviewState
    data class Loaded(
        val subscriptions: ImmutableList<SubscriptionWithPeriodInfo>,
        val filterState: SubscriptionFilterState,
        val detailId: SubscriptionId? = null,
    ) : OverviewState
}
