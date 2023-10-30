package dev.pott.abonity.app.navigation.components

import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.crashlytics.internal.model.ImmutableList
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.navigateTabItem

@Composable
fun AppPermanentDrawerSheet(
    tabs: ImmutableList<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    PermanentDrawerSheet(modifier = modifier) {
        tabs.forEach {
            DrawerItem(
                navigationItem = it,
                selected = it == selectedTab,
                onClick = { navController.navigateTabItem(it) },
            )
        }
    }
}
