package dev.pott.abonity.core.ui.components

import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = rememberVectorPainter(image = AppIcons.Close),
            contentDescription = stringResource(
                id = R.string.common_close_button_content_description
            )
        )
    }
}