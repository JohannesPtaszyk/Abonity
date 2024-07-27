package dev.pott.abonity.core.ui.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.pott.abonity.core.ui.preview.PreviewCommonUiConfig
import dev.pott.abonity.core.ui.theme.AppTheme

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
    text: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.titleMedium,
        LocalContentColor provides MaterialTheme.colorScheme.secondary,
    ) {
        Row(
            modifier = modifier.heightIn(min = ButtonDefaults.MinHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            text()
            action()
        }
    }
}

@PreviewCommonUiConfig
@Composable
private fun SectionHeaderPreview() {
    AppTheme {
        SectionHeader(
            action = {
                TextButton(onClick = {}) {
                    Text(text = "Action")
                }
            },
            text = {
                Text(text = "Title")
            },
        )
    }
}
