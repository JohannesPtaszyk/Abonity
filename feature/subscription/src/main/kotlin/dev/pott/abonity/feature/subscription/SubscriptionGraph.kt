package dev.pott.abonity.feature.subscription

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.core.entity.SubscriptionId
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
            val detailViewModel = hiltViewModel<DetailViewModel>()
            var lastNavigationId by rememberSaveable {
                mutableStateOf<Long?>(null)
            }
            if (state.showOverviewAsMultiColumn) {
                LaunchedEffect(lastNavigationId) {
                    val id = lastNavigationId ?: return@LaunchedEffect
                    lastNavigationId = null
                    overviewViewModel.openDetails(SubscriptionId(id))
                }
                OverviewScreenWithDetails(
                    overviewViewModel,
                    detailViewModel
                )
            } else {
                LaunchedEffect(state) {
                    lastNavigationId = null
                }
                OverviewScreen(
                    viewModel = overviewViewModel,
                    openDetails = { detailId ->
                        lastNavigationId = detailId.id
                        val args = DetailScreenDestination.Args(detailId.id)
                        navController.navigate(
                            DetailScreenDestination.getRouteWithArgs(args)
                        )
                    }
                )
            }
        }
        composable(DetailScreenDestination) {
            LaunchedEffect(state.showOverviewAsMultiColumn) {
                if (state.showOverviewAsMultiColumn) {
                    navController.popBackStack()
                }
            }
            DetailScreen(
                hiltViewModel(),
                close = { navController.popBackStack() },
            )
        }
        composable(AddScreenDestination) {
            AddScreen()
        }
    }
}
