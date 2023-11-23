package dev.pott.abonity.feature.subscription

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewRoute
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(
    state: SubscriptionGraphState,
    navController: NavController,
) {
    navigation(SubscriptionNavigationDestination) {
        composable(OverviewScreenDestination) {
            OverviewRoute(state.showOverviewAsMultiColumn, navController)
        }
        composable(AddScreenDestination) {
            AddScreen(close = { navController.popBackStack() })
        }
    }
}
