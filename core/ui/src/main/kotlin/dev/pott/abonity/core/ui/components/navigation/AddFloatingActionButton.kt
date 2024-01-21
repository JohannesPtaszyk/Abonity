package dev.pott.abonity.core.ui.components.navigation

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons

@Composable
fun AddFloatingActionButton(onClick: () -> Unit, expanded: Boolean, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        expanded = expanded,
        modifier = modifier,
        icon = {
            Icon(painter = rememberVectorPainter(image = AppIcons.Add), contentDescription = null)
        },
        text = { Text(text = stringResource(id = R.string.add_subscription_btn)) },
    )
}
