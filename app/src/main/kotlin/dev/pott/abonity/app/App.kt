package dev.pott.abonity.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.NavigationType
import dev.pott.abonity.app.navigation.appNavGraph
import dev.pott.abonity.app.navigation.components.NavigationIcon
import dev.pott.abonity.app.navigation.navigateTabItem
import dev.pott.abonity.app.navigation.rememberAppState
import dev.pott.abonity.core.ui.components.navigation.AddFloatingActionButton
import dev.pott.abonity.feature.subscription.add.AddDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddDestination

@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    openNotificationSettings: () -> Unit,
    openUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    promptAppStoreReview: () -> Unit,
) {
    val navController = rememberNavController()
    val appState = rememberAppState(navController, windowSizeClass)
    val startRoute = remember { NavigationItem.entries.first().destination }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (appState.navigationType == NavigationType.NAVIGATION_BAR) {
                BottomAppBar {
                    appState.navigationItems.forEach {
                        NavigationBarItem(
                            selected = appState.selectedNavigationItem == it,
                            onClick = {
                                navController.navigateTabItem(
                                    it,
                                    appState.selectedNavigationItem == it,
                                )
                            },
                            label = { Text(stringResource(id = it.titleRes)) },
                            icon = { NavigationIcon(navigationItem = it) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val fabScale by animateFloatAsState(
                targetValue = if (appState.shouldShowAddFloatingActionbutton) 1f else 0f,
                label = "FAB Scale Animation",
            )
            AddFloatingActionButton(
                onClick = { navController.navigate(AddDestination()) },
                expanded = false,
                modifier = Modifier.scale(scale = fabScale),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (appState.navigationType == NavigationType.NAVIGATION_RAIL) {
                NavigationRail {
                    AddFloatingActionButton(
                        onClick = { navController.navigateToAddDestination() },
                        expanded = false,
                    )
                    Spacer(Modifier.height(32.dp))
                    appState.navigationItems.forEach {
                        NavigationRailItem(
                            selected = appState.selectedNavigationItem == it,
                            onClick = {
                                navController.navigateTabItem(
                                    it,
                                    appState.selectedNavigationItem == it,
                                )
                            },
                            label = { Text(stringResource(id = it.titleRes)) },
                            icon = { NavigationIcon(navigationItem = it) },
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = startRoute,
            ) {
                appNavGraph(
                    appState,
                    navController,
                    openNotificationSettings,
                    openUrl = openUrl,
                    promptAppStoreReview = promptAppStoreReview,
                )
            }
        }
    }
}
