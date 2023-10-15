package dev.pott.abonity.app.navigation.components

import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.navigateTabItem

@Composable
fun AppNavigationRail(
    tabs: List<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        tabs.forEach {
            RailItem(
                it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) }
            )
        }
    }
}
