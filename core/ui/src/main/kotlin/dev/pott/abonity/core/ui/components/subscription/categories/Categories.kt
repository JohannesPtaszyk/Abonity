package dev.pott.abonity.core.ui.components.subscription.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.Category
import kotlinx.collections.immutable.ImmutableList

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun Categories(categories: ImmutableList<Category>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        categories.forEach { category ->
            AssistChip(
                onClick = {},
                label = { Text(text = category.name) },
            )
        }
    }
}
