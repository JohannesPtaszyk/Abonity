package dev.pott.abonity.feature.home

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.UpcomingPayments

sealed interface DashboardState {
    data object Loading : DashboardState

    data class Loaded(
        val upcomingPayments: UpcomingPayments,
        val selectedId: SubscriptionId?,
        val shouldShowNotificationTeaser: Boolean,
    ) : DashboardState
}
