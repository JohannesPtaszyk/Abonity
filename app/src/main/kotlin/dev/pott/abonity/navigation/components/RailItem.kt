package dev.pott.abonity.navigation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.navigation.NavigationItem

@Composable
fun RailItem(
    navigationItem: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = rememberVectorPainter(image = navigationItem.icon),
                contentDescription = stringResource(id = navigationItem.titleRes)
            )
        },
        label = {
            Text(stringResource(id = navigationItem.titleRes))
        },
        alwaysShowLabel = false
    )
}
