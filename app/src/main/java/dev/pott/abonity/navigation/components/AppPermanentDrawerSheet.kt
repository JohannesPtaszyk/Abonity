package dev.pott.abonity.navigation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.pott.abonity.navigateTabItem
import dev.pott.abonity.navigation.NavigationItem

@Composable
fun AppPermanentDrawerSheet(
    tabs: Array<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController
) {
    PermanentDrawerSheet(
        modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 16.dp
        )
    ) {
        tabs.forEach {
            DrawerItem(
                it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) }
            )
        }
    }
}