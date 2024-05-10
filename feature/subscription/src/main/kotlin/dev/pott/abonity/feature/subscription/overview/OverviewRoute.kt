package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onOpenCategoriesClick: () -> Unit,
    args: OverviewScreenDestination.Args?,
    modifier: Modifier = Modifier,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    detailViewModel: DetailViewModel = hiltViewModel(),
) {
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var skipFirstDetailAnimation by rememberSaveable {
        mutableStateOf(args?.detailId != null)
    }

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
            onEditClicked = onEditClick,
            onDeleteClicked = overviewViewModel::delete,
            closeDetails = overviewViewModel::consumeDetails,
            onOpenCategoriesClick = onOpenCategoriesClick,
            listState = listState,
        )
    } else {
        OverviewScreen(
            state = overviewState,
            onSubscriptionClick = overviewViewModel::openDetails,
            onFilterItemSelected = overviewViewModel::toggleFilter,
            onSwipeToDelete = overviewViewModel::delete,
            onOpenCategoriesClick = onOpenCategoriesClick,
            listState = listState,
        )
        val showDetail = (overviewState as? OverviewState.Loaded)?.detailId != null
        AnimatedVisibility(
            showDetail,
            enter = if (skipFirstDetailAnimation) {
                EnterTransition.None
            } else {
                slideInHorizontally { it } + fadeIn()
            },
            exit = slideOutHorizontally { it } + fadeOut(),
        ) {
            SideEffect {
                if (skipFirstDetailAnimation) {
                    skipFirstDetailAnimation = false
                }
            }
            BackHandler { overviewViewModel.consumeDetails() }
            DetailScreen(
                state = detailState,
                onEditClicked = onEditClick,
                onDeleteClicked = overviewViewModel::delete,
                close = overviewViewModel::consumeDetails,
            )
        }
    }
}
