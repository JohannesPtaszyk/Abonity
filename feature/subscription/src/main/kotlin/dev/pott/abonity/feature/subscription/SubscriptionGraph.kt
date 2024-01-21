package dev.pott.abonity.feature.subscription

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddScreen
import dev.pott.abonity.feature.subscription.overview.OverviewRoute
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.dialog
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(
    state: SubscriptionGraphState,
    navController: NavController,
) {
    navigation(
        destination = SubscriptionNavigationDestination,
        startDestination = OverviewScreenDestination,
    ) {
        composable(OverviewScreenDestination) {
            OverviewRoute(
                showAsMultiColumn = state.showOverviewAsMultiColumn,
                onEditClick = { navController.navigateToAddScreen(it) },
            )
        }
    }
    dialog(
        AddScreenDestination,
        DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        AddScreen(close = { navController.popBackStack() })
    }
}
