package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.ui.components.navigation.AddFloatingActionButton
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailViewModel
import kotlinx.coroutines.delay

private const val FAB_COLLAPSE_DELAY = 300L

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OverviewRoute(
    showAsMultiColumn: Boolean,
    showAddFloatingActionButton: Boolean,
    onEditClick: (SubscriptionId) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    detailViewModel: DetailViewModel = hiltViewModel(),
) {
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(overviewState.detailId) {
        detailViewModel.setId(overviewState.detailId)
    }
    if (showAsMultiColumn) {
        OverviewScreenWithDetails(
            modifier = modifier,
            overviewState = overviewState,
            detailState = detailState,
            onSubscriptionClicked = overviewViewModel::openDetails,
            onEditClick = onEditClick,
            closeDetails = overviewViewModel::consumeDetails,
            listState = listState,
        )
        return
    }
    if (overviewState.detailId != null) {
        BackHandler { overviewViewModel.consumeDetails() }
        DetailScreen(
            state = detailState,
            onEditClick = onEditClick,
            close = overviewViewModel::consumeDetails,
        )
    } else {
        val scrollInProgress by remember { derivedStateOf { listState.isScrollInProgress } }
        var isFabExpanded by remember { mutableStateOf(true) }
        LaunchedEffect(scrollInProgress) {
            isFabExpanded = if (scrollInProgress) {
                false
            } else {
                delay(FAB_COLLAPSE_DELAY)
                true
            }
        }
        OverviewScreen(
            state = overviewState,
            onSubscriptionClick = overviewViewModel::openDetails,
            floatingActionButton = {
                if (showAddFloatingActionButton) {
                    AddFloatingActionButton(
                        onClick = onAddClick,
                        expanded = isFabExpanded,
                    )
                }
            },
            listState = listState,
        )
    }
}
