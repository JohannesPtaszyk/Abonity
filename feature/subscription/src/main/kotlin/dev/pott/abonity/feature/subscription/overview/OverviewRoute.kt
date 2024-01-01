package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailViewModel

@Composable
fun OverviewRoute(
    showAsMultiColumn: Boolean,
    onEditClick: (SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    detailViewModel: DetailViewModel = hiltViewModel(),
) {
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(overviewState) {
        val loadedState = (overviewState as? OverviewState.Loaded) ?: return@LaunchedEffect
        detailViewModel.setId(loadedState.detailId)
    }

    if (showAsMultiColumn) {
        OverviewScreenWithDetails(
            modifier = modifier,
            overviewState = overviewState,
            detailState = detailState,
            onSubscriptionClicked = overviewViewModel::openDetails,
            onFilterItemSelected = overviewViewModel::toggleFilter,
            onEditClick = onEditClick,
            closeDetails = overviewViewModel::consumeDetails,
            listState = listState,
        )
        return
    }

    if ((overviewState as? OverviewState.Loaded)?.detailId != null) {
        BackHandler { overviewViewModel.consumeDetails() }
        DetailScreen(
            state = detailState,
            onEditClick = onEditClick,
            close = overviewViewModel::consumeDetails,
        )
    } else {
        OverviewScreen(
            state = overviewState,
            onSubscriptionClick = overviewViewModel::openDetails,
            onFilterItemSelected = overviewViewModel::toggleFilter,
            listState = listState,
        )
    }
}
