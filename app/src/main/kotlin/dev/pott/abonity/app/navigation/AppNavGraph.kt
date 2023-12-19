package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.home.homeGraph
import dev.pott.abonity.feature.settings.SettingsScreen
import dev.pott.abonity.feature.settings.SettingsScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.setDestination

fun NavGraphBuilder.appNavGraph(state: AppState, navController: NavController) {
    homeGraph(
        openDetails = { subscriptionId ->
            navController.createDeepLink()
                .setDestination(
                    OverviewScreenDestination,
                    OverviewScreenDestination.Args(subscriptionId),
                )
                .createPendingIntent()
                .send()
        },
        openSubscriptions = { navController.navigateTabItem(NavigationItem.SUBSCRIPTION) },
    )
    subscriptionGraph(state.subscriptionGraphState, navController)
    composable(SettingsScreenDestination) { SettingsScreen() }
}
