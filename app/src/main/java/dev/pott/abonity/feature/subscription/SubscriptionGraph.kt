package dev.pott.abonity.feature.subscription

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewScreen
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(navController: NavController) {
    navigation(SubscriptionNavigationDestination) {
        composable(OverviewScreenDestination) {
            OverviewScreen()
        }
        composable(DetailScreenDestination) {
            DetailScreen()
        }
        composable(AddScreenDestination) {
            AddScreen()
        }
    }
}