package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.feature.subscription.components.SubscriptionCard

private const val OVERVIEW_PANE_SPLIT_FRACTION = 0.5f

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel,
    openDetails: (id: SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val detailId = state.detailId
    LaunchedEffect(detailId) {
        if (detailId == null) return@LaunchedEffect
        openDetails(detailId)
        viewModel.consumeDetails()
    }

    OverviewScreen(state, viewModel::openDetails, modifier)
}

@Composable
fun OverviewScreen(
    state: OverviewState,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { paddingValues ->
        val listPaddingValues = PaddingValues(
            vertical = 16.dp,
            horizontal = 16.dp
        ) + paddingValues
        SubscriptionList(
            listPaddingValues,
            state,
            onSubscriptionClick,
            modifier
        )
    }
}

@Composable
private fun SubscriptionList(
    paddingValues: PaddingValues,
    state: OverviewState,
    onClick: (id: SubscriptionId) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        items(state.subscriptions) { subscriptionItem ->
            SubscriptionCard(
                subscriptionItem,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                onClick(subscriptionItem.subscription.id)
            }
        }
    }
}
