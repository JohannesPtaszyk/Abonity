package dev.pott.abonity.navigation.components

import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.navigateTabItem

@Composable
fun AppBottomBar(
    tabs: Array<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController
) {
    BottomAppBar {
        tabs.forEach {
            BottomBarItem(
                it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) }
            )
        }
    }
}
