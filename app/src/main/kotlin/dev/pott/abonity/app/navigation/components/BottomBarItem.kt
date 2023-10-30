package dev.pott.abonity.app.navigation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.app.navigation.NavigationItem

@Composable
fun RowScope.BottomBarItem(
    navigationItem: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = { NavigationIcon(navigationItem) },
        label = {
            Text(stringResource(id = navigationItem.titleRes))
        },
        alwaysShowLabel = false,
    )
}
