package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.ui.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionFilter(
    state: SubscriptionFilter,
    onItemSelect: (SubscriptionFilterItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.selectedItems) {
        listState.animateScrollToItem(0)
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
        state = listState,
    ) {
        items(state.items, key = { it.hashCode() }) {
            FilterChip(
                onClick = { onItemSelect(it) },
                selected = state.selectedItems.contains(it),
                label = {
                    when (it) {
                        is SubscriptionFilterItem.Currency -> {
                            FormattedPrice(price = it.price)
                        }

                        is SubscriptionFilterItem.CurrentPeriod -> {
                            CurrentPeriodLabel(it)
                        }

                        is SubscriptionFilterItem.Category -> {
                            Text(text = it.category.name)
                        }
                    }
                },
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun CurrentPeriodLabel(it: SubscriptionFilterItem.CurrentPeriod) {
    val text = when (it.period) {
        PaymentPeriod.DAYS -> R.string.subscription_filter_current_period_day
        PaymentPeriod.WEEKS -> R.string.subscription_filter_current_period_week
        PaymentPeriod.MONTHS -> R.string.subscription_filter_current_period_month
        PaymentPeriod.YEARS -> R.string.subscription_filter_current_period_year
    }
    Text(text = stringResource(id = text))
}
