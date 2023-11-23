package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddScreen
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OverviewRoute(
    showAsMultiColumn: Boolean,
    navController: NavController,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    detailViewModel: DetailViewModel = hiltViewModel(),
) {
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    SideEffect {
        detailViewModel.setId(overviewState.detailId)
    }
    if (showAsMultiColumn) {
        OverviewScreenWithDetails(
            overviewState = overviewState,
            detailState = detailState,
            onSubscriptionClicked = overviewViewModel::openDetails,
            onEditClick = {
                val args = AddScreenDestination.Args(it.id)
                navController.navigate(
                    AddScreenDestination.getRouteWithArgs(args),
                )
            },
            closeDetails = overviewViewModel::consumeDetails,
            openAdd = { navController.navigateToAddScreen() },
        )
    } else {
        if (overviewState.detailId != null) {
            BackHandler { overviewViewModel.consumeDetails() }
            DetailScreen(
                state = detailState,
                onEditClick = {
                    val args = AddScreenDestination.Args(it.id)
                    navController.navigate(
                        AddScreenDestination.getRouteWithArgs(args),
                    )
                },
                close = overviewViewModel::consumeDetails,
            )
        } else {
            OverviewScreen(
                state = overviewState,
                onSubscriptionClick = overviewViewModel::openDetails,
                onAddClick = { navController.navigateToAddScreen() },
            )
        }
    }
}
