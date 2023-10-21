package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.util.getActivity
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailState
import dev.pott.abonity.feature.subscription.detail.DetailViewModel

@Composable
fun OverviewScreenWithDetails(
    overviewViewModel: OverviewViewModel,
    detailViewModel: DetailViewModel
) {
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = overviewState.detailId != null) {
        overviewViewModel.consumeDetails()
    }
    LaunchedEffect(overviewState.detailId) {
        detailViewModel.setId(overviewState.detailId)
    }
    OverViewScreenWithDetails(
        overviewState,
        detailState,
        overviewViewModel::openDetails
    )
}

@Composable
private fun OverViewScreenWithDetails(
    overviewState: OverviewState,
    detailState: DetailState,
    onSubscriptionClicked: (id: SubscriptionId) -> Unit,
) {
    val activity = LocalContext.current.getActivity()
    TwoPane(
        first = {
            OverviewScreen(
                state = overviewState,
                onSubscriptionClick = onSubscriptionClicked
            )
        },
        second = {
            DetailScreen(state = detailState)
        },
        strategy = HorizontalTwoPaneStrategy(0.5f, 24.dp),
        displayFeatures = calculateDisplayFeatures(activity)
    )
}