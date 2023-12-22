package dev.pott.abonity.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.appNavGraph
import dev.pott.abonity.app.navigation.components.NavigationIcon
import dev.pott.abonity.app.navigation.navigateTabItem
import dev.pott.abonity.app.navigation.rememberAppState

@Composable
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
fun App() {
    val navController = rememberNavController()
    val appState = rememberAppState(navController)
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.navigationItems.forEach {
                item(
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
        },
        layoutType = appState.navigationType,
    ) {
        val startRoute = remember {
            NavigationItem.entries.first().destination.route
        }
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = if (appState.navigationType == NavigationSuiteType.NavigationBar) {
                Modifier.consumeWindowInsets(WindowInsets.navigationBars)
            } else {
                Modifier
            },
        ) {
            appNavGraph(
                appState,
                navController,
            )
        }
    }
}
