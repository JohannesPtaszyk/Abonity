package dev.pott.abonity.feature.subscription

import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.navigation.destination.Destination
import dev.pott.abonity.navigation.destination.NoArgNavigationDestination

object SubscriptionNavigationDestination : NoArgNavigationDestination("subscription") {

    override val startDestination: Destination<*> = OverviewScreenDestination
}
