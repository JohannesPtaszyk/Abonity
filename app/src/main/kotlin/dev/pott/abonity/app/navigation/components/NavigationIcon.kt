package dev.pott.abonity.app.navigation.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.app.navigation.NavigationItem

@Composable
fun NavigationIcon(navigationItem: NavigationItem, modifier: Modifier = Modifier) {
    Icon(
        painter = rememberVectorPainter(image = navigationItem.icon),
        contentDescription = stringResource(id = navigationItem.titleRes),
        modifier = modifier,
    )
}
