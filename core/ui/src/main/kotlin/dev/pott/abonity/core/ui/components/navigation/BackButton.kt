package dev.pott.abonity.core.ui.components.navigation

import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = rememberVectorPainter(image = AppIcons.ArrowBack),
            contentDescription = stringResource(
                id = R.string.common_back_button_content_description,
            ),
        )
    }
}
