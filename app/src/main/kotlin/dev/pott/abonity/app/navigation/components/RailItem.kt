package dev.pott.abonity.app.navigation.components

import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.app.navigation.NavigationItem

@Composable
fun RailItem(
    navigationItem: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = { NavigationIcon(navigationItem = navigationItem) },
        label = { Text(stringResource(id = navigationItem.titleRes)) },
        alwaysShowLabel = false,
    )
}
