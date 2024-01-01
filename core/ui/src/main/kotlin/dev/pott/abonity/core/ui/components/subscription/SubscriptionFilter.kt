package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.string.paymentPeriodPluralRes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SubscriptionFilter(
    state: SubscriptionFilterState,
    onItemSelected: (SubscriptionFilterItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.selectedItems) {
        listState.scrollToItem(0)
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
        state = listState,
    ) {
        items(state.items, key = { it.hashCode() }) {
            FilterChip(
                onClick = { onItemSelected(it) },
                selected = state.selectedItems.contains(it),
                label = {
                    when (it) {
                        is SubscriptionFilterItem.Currency -> {
                            FormattedPrice(price = it.price)
                        }

                        is SubscriptionFilterItem.CurrentPeriod -> {
                            Text(
                                text = stringResource(
                                    id = R.string.subscription_filter_current_period,
                                    pluralStringResource(
                                        id = paymentPeriodPluralRes(it = it.period),
                                        count = 1,
                                    ),
                                ),
                            )
                        }
                    }
                },
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

sealed interface SubscriptionFilterItem {

    data class Currency(val price: Price) : SubscriptionFilterItem

    data class CurrentPeriod(val period: PaymentPeriod) : SubscriptionFilterItem
}

/**
 * Represents Subscription Filter State.
 *
 * Primary constructor is private to make sure items always include default options, which are not
 * based on user data.
 */
class SubscriptionFilterState private constructor(
    val items: ImmutableList<SubscriptionFilterItem>,
    val selectedItems: ImmutableList<SubscriptionFilterItem>,
) {

    constructor(
        prices: List<Price>,
        period: PaymentPeriod,
        selectedItems: List<SubscriptionFilterItem>,
    ) : this(
        items = buildList<SubscriptionFilterItem> {
            add(SubscriptionFilterItem.CurrentPeriod(period))
            addAll(prices.map { SubscriptionFilterItem.Currency(it) })
        }.sortedBy {
            !selectedItems.contains(it)
        }.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubscriptionFilterState

        if (items != other.items) return false
        return selectedItems == other.selectedItems
    }

    override fun hashCode(): Int {
        var result = items.hashCode()
        result = 31 * result + selectedItems.hashCode()
        return result
    }
}
