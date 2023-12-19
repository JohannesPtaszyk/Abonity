package dev.pott.abonity.feature.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.homeGraph(
    openDetails: (subscriptionId: SubscriptionId) -> Unit,
    openSubscriptions: () -> Unit,
) {
    navigation(
        destination = HomeNavigationDestination,
        startDestination = DashboardScreenDestination,
    ) {
        composable(DashboardScreenDestination) {
            HomeScreen(
                viewModel = hiltViewModel(),
                openDetails = openDetails,
                openSubscriptions = openSubscriptions,
            )
        }
    }
}
