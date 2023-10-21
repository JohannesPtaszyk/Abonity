package dev.pott.abonity.feature.subscription.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.feature.subscription.overview.SubscriptionItem

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DetailScreen(state = state, modifier = modifier)
}

@Composable
fun DetailScreen(state: DetailState, modifier: Modifier = Modifier) {
    Scaffold {
        val subscription = state.subscription
        if (subscription != null) {
            SubscriptionDetails(
                subscription,
                modifier.padding(paddingValues = it)
            )
        } else {
            Text(text = "Click on a card to open details!")
        }
    }
}

@Composable
private fun SubscriptionDetails(
    subscription: SubscriptionItem,
    modifier: Modifier,
) {
    Text(
        text = subscription.toString(),
        modifier = modifier
    )
}
