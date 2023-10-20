package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.feature.subscription.components.SubscriptionOverviewCard

@Composable
fun OverviewScreen(
    viewModel: SubscriptionOverviewViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    OverviewScreen(state, modifier)
}

@Composable
private fun OverviewScreen(
    state: SubscriptionOverviewState,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                vertical = 16.dp,
                horizontal = 16.dp
            ) + paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier,
        ) {
            items(state.subscriptions) {
                SubscriptionOverviewCard(
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {}
            }
        }
    }
}
