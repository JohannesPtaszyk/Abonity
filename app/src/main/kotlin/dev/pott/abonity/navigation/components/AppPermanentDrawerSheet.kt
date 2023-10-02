package dev.pott.abonity.navigation.components

import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.navigateTabItem

@Composable
fun AppPermanentDrawerSheet(
    tabs: Array<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    PermanentDrawerSheet(modifier = modifier) {
        tabs.forEach {
            DrawerItem(
                navigationItem = it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) }
            )
        }
    }
}
