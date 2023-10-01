package dev.pott.abonity.navigation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.navigateTabItem

@Composable
fun AppNavigationRail(
    tabs: Array<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController
) {
    NavigationRail(
        modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 16.dp
        )
    ) {
        tabs.forEach {
            RailItem(
                it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) }
            )
        }
    }
}