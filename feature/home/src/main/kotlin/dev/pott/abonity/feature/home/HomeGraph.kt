package dev.pott.abonity.feature.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.homeGraph(
    openDetails: (subscriptionId: SubscriptionId) -> Unit,
    openSubscriptions: () -> Unit,
    openNotificationSettings: () -> Unit,
    openAddScreen: () -> Unit,
) {
    navigation(
        destination = HomeNavigationDestination,
        startDestination = DashboardScreenDestination,
    ) {
        composable(DashboardScreenDestination) {
            DashboardScreen(
                viewModel = hiltViewModel(),
                openDetails = openDetails,
                openSubscriptions = openSubscriptions,
                openNotificationSettings = openNotificationSettings,
                openAddScreen = openAddScreen,
            )
        }
    }
}
