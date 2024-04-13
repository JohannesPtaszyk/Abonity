package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.pott.abonity.common.text.rememberPriceValueFilter
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.util.rememberDefaultLocale
import dev.pott.abonity.feature.subscription.add.ValidatedInput
import dev.pott.abonity.feature.subscription.add.localizedError
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency

private const val PRICE_PLACEHOLDER = 9.99

@Composable
fun PriceInput(
    priceValue: ValidatedInput,
    currency: Currency,
    onPriceChanged: (priceValue: String) -> Unit,
    onCurrencyChanged: (currency: Currency) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }
    val locale = rememberDefaultLocale()
    Row(modifier = modifier) {
        TextField(
            label = { Text(text = stringResource(id = R.string.subscription_add_label_price)) },
            placeholder = {
                val format = remember(locale) {
                    NumberFormat.getInstance(locale)
                }
                Text(text = format.format(PRICE_PLACEHOLDER), Modifier.alpha(alpha = 0.8f))
            },
            value = priceValue.value,
            onValueChange = rememberPriceValueFilter(onPriceChanged),
            isError = priceValue.isError,
            supportingText = {
                priceValue.localizedError()?.let { Text(text = it) }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal,
            ),
            modifier = Modifier.weight(1f, fill = true),
        )
        Spacer(modifier = Modifier.width(8.dp))
        val source = remember { MutableInteractionSource() }
        val isPressed by source.collectIsPressedAsState()
        LaunchedEffect(isPressed) {
            if (isPressed) {
                showCurrencyBottomSheet = true
            }
        }
        TextField(
            readOnly = true,
            value = currency.getSymbol(locale),
            onValueChange = {},
            label = { Text(stringResource(id = R.string.subscription_add_currency_label)) },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, null)
            },
            interactionSource = source,
            modifier = Modifier.weight(weight = 0.6f),
        )
    }

    if (showCurrencyBottomSheet) {
        CurrencyBottomSheet(
            onCurrencyChanged = {
                showCurrencyBottomSheet = false
                onCurrencyChanged(it)
            },
            onDismissRequest = { showCurrencyBottomSheet = false },
        )
    }
}

private const val LOADING_INDICATOR_ITEM = "LoadingIndicator"
private const val SPACER_ITEM = "Spacer"
private const val CURRENCY_ITEM = "Currency"

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
private fun CurrencyBottomSheet(
    onCurrencyChanged: (currency: Currency) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val locale = rememberDefaultLocale()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(),
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        var options by remember { mutableStateOf(emptyList<Currency>()) }
        var searchQuery by remember { mutableStateOf("") }

        LaunchedEffect(searchQuery) {
            withContext(Dispatchers.Default) {
                val filtered = Currency.getAvailableCurrencies().let { currencies ->
                    if (searchQuery.isEmpty()) {
                        currencies
                    } else {
                        currencies.filter { currency ->
                            currency.getDisplayName(locale).contains(searchQuery)
                        }
                    }.sortedBy {
                        it.getDisplayName(locale)
                    }.toImmutableList()
                }
                withContext(Dispatchers.Main) { options = filtered }
            }
        }

        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(
                        painter = rememberVectorPainter(image = AppIcons.Search),
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            painter = rememberVectorPainter(image = AppIcons.Clear),
                            contentDescription = stringResource(
                                id = R.string.add_subscription_currency_search_clear_label,
                            ),
                        )
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                if (options.isEmpty() && searchQuery.isBlank()) {
                    item(key = LOADING_INDICATOR_ITEM, contentType = LOADING_INDICATOR_ITEM) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 200.dp),
                        ) {
                            CircularProgressIndicator(modifier = Modifier.animateItemPlacement())
                        }
                    }
                }
                items(options, key = { it.currencyCode }, contentType = { CURRENCY_ITEM }) {
                    ListItem(
                        leadingContent = {
                            Text(
                                text = it.getSymbol(locale),
                                modifier = Modifier.defaultMinSize(minWidth = 50.dp),
                            )
                        },
                        headlineContent = { Text(text = it.getDisplayName(locale)) },
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillMaxWidth()
                            .clickable { onCurrencyChanged(it) },
                    )
                }
                item(key = SPACER_ITEM, contentType = SPACER_ITEM) {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}
