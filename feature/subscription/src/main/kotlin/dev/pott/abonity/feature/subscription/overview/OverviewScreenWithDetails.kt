package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
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
        Logger.i("Consume details ${overviewState.detailId}")
        overviewViewModel.consumeDetails()
    }
    LaunchedEffect(overviewState.detailId) {
        detailViewModel.setId(overviewState.detailId)
    }
    OverViewScreenWithDetails(
        overviewState,
        detailState,
        overviewViewModel::openDetails,
        overviewViewModel::consumeDetails,
    )
}

@Composable
private fun OverViewScreenWithDetails(
    overviewState: OverviewState,
    detailState: DetailState,
    onSubscriptionClicked: (id: SubscriptionId) -> Unit,
    closeDetails: () -> Unit,
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
            AnimatedVisibility(
                label = "Detail animated visibility",
                visible = detailState.subscription?.id != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                DetailScreen(
                    state = detailState,
                    close = closeDetails
                )
            }
        },
        strategy = HorizontalTwoPaneStrategy(0.5f, 24.dp),
        displayFeatures = calculateDisplayFeatures(activity)
    )
}