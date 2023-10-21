package dev.pott.abonity.feature.subscription

import androidx.activity.compose.BackHandler
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.util.getActivity
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
            if (state.showOverviewAsMultiColumn) {
                OverviewScreenWithDetails(
                    overviewViewModel,
                    detailViewModel
                )
            } else {
                OverviewScreen(
                    viewModel = overviewViewModel,
                    openDetails = {
                        detailViewModel.setId(it)
                        val args = DetailScreenDestination.Args(it.id)
                        navController.navigate(
                            DetailScreenDestination.getRouteWithArgs(args)
                        )
                    }
                )
            }
        }
        composable(DetailScreenDestination) {
            DetailScreen(hiltViewModel())
        }
        composable(AddScreenDestination) {
            AddScreen()
        }
    }
}
