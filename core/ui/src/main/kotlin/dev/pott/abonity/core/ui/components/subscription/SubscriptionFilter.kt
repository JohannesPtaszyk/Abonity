package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.string.paymentPeriodPluralRes
import dev.pott.abonity.core.ui.util.rememberDefaultLocale

@Composable
fun SubscriptionFilter(
    filter: SubscriptionFilter,
    period: PaymentPeriod,
    onItemSelect: (SubscriptionFilterItem) -> Unit,
    onPeriodChange: (PaymentPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(filter.selectedItems) {
        listState.animateScrollToItem(0)
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
        state = listState,
    ) {
        periodFilterItem(period, onPeriodChange)
        items(filter.items, key = { it.hashCode() }) {
            FilterChip(
                onClick = { onItemSelect(it) },
                selected = filter.selectedItems.contains(it),
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
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.periodFilterItem(
    period: PaymentPeriod,
    onPeriodChange: (PaymentPeriod) -> Unit,
) {
    item {
        var showPeriodDropdown by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = showPeriodDropdown,
            onExpandedChange = { showPeriodDropdown = !showPeriodDropdown },
            modifier = Modifier.animateItem(),
        ) {
            val chipLabelRes = paymentPeriodPluralRes(period)
            val locale = rememberDefaultLocale()
            FilterChip(
                onClick = { /* Click will be handled by menuAnchor */ },
                selected = true,
                label = {
                    Text(
                        text = pluralStringResource(
                            id = chipLabelRes,
                            count = 1,
                        ).replaceFirstChar { it.titlecase(locale) },
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPeriodDropdown)
                },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = showPeriodDropdown,
                onDismissRequest = { showPeriodDropdown = false },
                matchTextFieldWidth = false,
            ) {
                val items = remember { PaymentPeriod.entries }
                items.forEach { item ->
                    val labelRes = paymentPeriodPluralRes(item)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = pluralStringResource(
                                    id = labelRes,
                                    count = 1,
                                ).replaceFirstChar { it.titlecase(locale) },
                            )
                        },
                        onClick = {
                            onPeriodChange(item)
                            showPeriodDropdown = false
                        },
                        enabled = item != period,
                    )
                }
            }
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
