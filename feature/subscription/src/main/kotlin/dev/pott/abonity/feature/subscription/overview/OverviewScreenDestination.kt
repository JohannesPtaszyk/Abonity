package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.NoArgNestedDestination

private const val OVERVIEW_ROUTE = "overview"

object OverviewScreenDestination : NoArgNestedDestination(
    SubscriptionNavigationDestination,
    OVERVIEW_ROUTE,
)
