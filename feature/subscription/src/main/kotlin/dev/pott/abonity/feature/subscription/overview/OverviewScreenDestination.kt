package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.NoArgNestedDestination

object OverviewScreenDestination : NoArgNestedDestination(
    SubscriptionNavigationDestination,
    "overview"
)
