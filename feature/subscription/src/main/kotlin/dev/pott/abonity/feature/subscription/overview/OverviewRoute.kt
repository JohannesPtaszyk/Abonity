package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OverviewRoute(
    showAsMultiColumn: Boolean,
    onEditClicked: (SubscriptionId) -> Unit,
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
            onEditClick = onEditClicked,
            closeDetails = overviewViewModel::consumeDetails,
        )
    } else {
        if (overviewState.detailId != null) {
            BackHandler { overviewViewModel.consumeDetails() }
            DetailScreen(
                state = detailState,
                onEditClick = onEditClicked,
                close = overviewViewModel::consumeDetails,
            )
        } else {
            OverviewScreen(
                state = overviewState,
                onSubscriptionClick = overviewViewModel::openDetails,
            )
        }
    }
}
