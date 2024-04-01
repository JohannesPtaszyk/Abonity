package dev.pott.abonity.app

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddScreen

@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    openNotificationSettings: () -> Unit,
    openUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val appState = rememberAppState(navController, windowSizeClass)
    val startRoute = remember { NavigationItem.entries.first().destination.route }
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
            AnimatedVisibility(
                visible = appState.shouldShowAddFloatingActionbutton,
            ) {
                AddFloatingActionButton(
                    onClick = { navController.navigate(AddScreenDestination.route) },
                    expanded = false,
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (appState.navigationType == NavigationType.NAVIGATION_RAIL) {
                NavigationRail {
                    AddFloatingActionButton(
                        onClick = { navController.navigateToAddScreen() },
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
                )
            }
        }
    }
}
