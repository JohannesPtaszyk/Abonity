package dev.pott.abonity.feature.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.pott.abonity.core.entity.subscription.SubscriptionId

fun NavGraphBuilder.homeGraph(
    openDetails: (subscriptionId: SubscriptionId) -> Unit,
    openSubscriptions: () -> Unit,
    openNotificationSettings: () -> Unit,
    openAddScreen: () -> Unit,
) {
    navigation<HomeNavigationDestination>(
        startDestination = DashboardDestination,
    ) {
        composable<DashboardDestination> {
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
