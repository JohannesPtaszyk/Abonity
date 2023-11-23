package dev.pott.abonity.feature.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.navigation.destination.composable

fun NavGraphBuilder.homeGraph(openDetails: (subscriptionId: SubscriptionId) -> Unit) {
    composable(HomeScreenDestination) {
        HomeScreen(
            viewModel = hiltViewModel<HomeScreenViewModel>(),
            openDetails = openDetails,
            openAdd = { /*TODO*/ },
        )
    }
}
