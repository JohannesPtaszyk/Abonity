package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.pott.abonity.common.text.PriceValueTextFieldFilter
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.util.rememberDefaultLocale
import kotlinx.collections.immutable.toImmutableList
import java.text.NumberFormat
import java.util.Currency

private const val PRICE_PLACEHOLDER = 9.99

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceInput(
    priceValue: String,
    currency: Currency,
    onPriceChanged: (priceValue: String) -> Unit,
    onCurrencyChanged: (currency: Currency) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
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
            value = priceValue,
            onValueChange = PriceValueTextFieldFilter(onPriceChanged),
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
            label = { Text("Currency") },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, null)
            },
            interactionSource = source,
            modifier = Modifier.weight(weight = 0.6f),
        )
    }

    if (showCurrencyBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCurrencyBottomSheet = !showCurrencyBottomSheet
            },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            val options = remember { Currency.getAvailableCurrencies().toImmutableList() }
            LazyColumn {
                items(options) {
                    ListItem(
                        headlineContent = { Text(text = it.getDisplayName(locale)) },
                        modifier = Modifier.clickable { onCurrencyChanged(it) },
                    )
                }
                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}
