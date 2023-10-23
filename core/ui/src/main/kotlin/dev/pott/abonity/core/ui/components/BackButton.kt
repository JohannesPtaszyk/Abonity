package dev.pott.abonity.core.ui.components

import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = rememberVectorPainter(image = AppIcons.ArrowBack),
            contentDescription = stringResource(
                id = R.string.common_back_button_content_description
            )
        )
    }
}