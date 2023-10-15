package dev.pott.abonity.feature.subscription

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailScreenDestination
import dev.pott.abonity.feature.subscription.overview.OverviewScreen
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(state: SubscriptionGraphState) {
    navigation(SubscriptionNavigationDestination) {
        composable(OverviewScreenDestination) {
            Row {
                OverviewScreen(
                    viewModel = hiltViewModel(),
                    modifier = Modifier.weight(1f)
                )
                if (state.showOverviewAsMultiColumn) {
                    // TODO add detail for two pane layout here
                    Text(
                        "Place details here",
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    )
                }
            }
        }
        composable(DetailScreenDestination) {
            DetailScreen()
        }
        composable(AddScreenDestination) {
            AddScreen()
        }
    }
}
