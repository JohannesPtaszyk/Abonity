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
        LazyColumn(
            contentPadding = paddingValues + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier,
        ) {
            items(
                state.periodSubscriptions,
                key = { it.subscription.id.id },
                contentType = { "Subscription Card" }
            ) { subscriptionWithPeriodPrice ->
                SubscriptionCard(
                    subscriptionWithPeriodPrice.subscription,
                    subscriptionWithPeriodPrice.periodPrice,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubscriptionClick(subscriptionWithPeriodPrice.subscription.id)
                    },
                    isSelected = subscriptionWithPeriodPrice.isSelected
                )
            }
        }
    }
}
