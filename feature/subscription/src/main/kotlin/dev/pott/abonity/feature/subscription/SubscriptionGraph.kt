package dev.pott.abonity.feature.subscription

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailScreenDestination
import dev.pott.abonity.feature.subscription.detail.DetailViewModel
import dev.pott.abonity.feature.subscription.overview.OverviewScreen
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewScreenWithDetails
import dev.pott.abonity.feature.subscription.overview.OverviewViewModel
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(
    state: SubscriptionGraphState,
    navController: NavController
) {
    navigation(SubscriptionNavigationDestination) {
        composable(OverviewScreenDestination) {
            val overviewViewModel = hiltViewModel<OverviewViewModel>()
            if (state.showOverviewAsMultiColumn) {
                val detailViewModel = hiltViewModel<DetailViewModel>()
                OverviewScreenWithDetails(
                    overviewViewModel,
                    detailViewModel
                )
            } else {
                OverviewScreen(
                    viewModel = overviewViewModel,
                    openDetails = { detailId ->
                        val args = DetailScreenDestination.Args(detailId.id)
                        navController.navigate(
                            DetailScreenDestination.getRouteWithArgs(args)
                        )
                    }
                )
            }
        }
        composable(DetailScreenDestination) {
            val overviewViewModel = navController.previousBackStackEntry?.let {
                hiltViewModel<OverviewViewModel>(viewModelStoreOwner = it)
            }
            LaunchedEffect(state.showOverviewAsMultiColumn) {
                if (state.showOverviewAsMultiColumn) {
                    navController.popBackStack()
                }
            }
            DetailScreen(
                hiltViewModel(),
                onBackClick = {
                    overviewViewModel?.consumeDetails()
                    navController.popBackStack()
                },
            )
        }
        composable(AddScreenDestination) {
            AddScreen()
        }
    }
}
