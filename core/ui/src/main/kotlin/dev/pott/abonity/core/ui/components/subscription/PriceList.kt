package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.ui.components.text.SectionHeader
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PriceOverview(
    periodPrices: ImmutableList<Price>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionHeader(Modifier.padding(horizontal = 16.dp)) { title() }
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(periodPrices) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                ) {
                    FormattedPrice(
                        price = it,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        }
    }
}
