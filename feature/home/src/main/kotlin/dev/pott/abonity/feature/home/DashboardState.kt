package dev.pott.abonity.feature.home

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DashboardState(
    val upcomingSubscriptions: ImmutableList<SubscriptionWithPeriodInfo> = persistentListOf(),
    val selectedId: SubscriptionId? = null,
    val shouldShowNotificationTeaser: Boolean = false,
)
