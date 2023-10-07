package dev.pott.abonity.app

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.appNavGraph
import dev.pott.abonity.app.navigation.components.AppBottomBar
import dev.pott.abonity.app.navigation.components.AppNavigationRail
import dev.pott.abonity.app.navigation.components.AppPermanentDrawerSheet
import dev.pott.abonity.app.navigation.components.NavigationType
import dev.pott.abonity.app.navigation.components.rememberNavigationType
import dev.pott.abonity.core.ui.theme.AppTheme

@Composable
fun AppMainContent(activity: Activity, modifier: Modifier = Modifier) {
    AppTheme {
        val navigationType by rememberNavigationType(activity)
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val tabs = remember { NavigationItem.values() }
        val selectedTab by remember(currentDestination) {
            derivedStateOf {
                tabs.find { navigationItem ->
                    currentDestination?.hierarchy?.find {
                        navigationItem.destination.route == it.route
                    } != null
                }
            }
        }
        Scaffold(
            modifier = modifier,
            bottomBar = {
                AnimatedVisibility(
                    navigationType == NavigationType.BOTTOM,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    AppBottomBar(tabs, selectedTab, navController)
                }
            }
        ) { innerPadding ->
            AppMainScaffoldContent(
                navigationType,
                tabs,
                selectedTab,
                navController,
                innerPadding
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun AppMainScaffoldContent(
    navigationType: NavigationType,
    tabs: Array<NavigationItem>,
    selectedTab: NavigationItem?,
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            AnimatedContent(
                navigationType,
                label = "NavigationTypeTransition"
            ) { targetNavigationType ->
                when (targetNavigationType) {
                    NavigationType.DRAWER -> {
                        AppPermanentDrawerSheet(
                            tabs,
                            selectedTab,
                            navController,
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 16.dp
                            )
                        )
                    }

                    NavigationType.RAIL -> {
                        AppNavigationRail(
                            tabs,
                            selectedTab,
                            navController,
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 16.dp
                            )
                        )
                    }

                    NavigationType.BOTTOM -> {
                        // Bottom bar will be rendered in scaffold
                    }
                }
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.entries.first().destination.route,
                Modifier.padding(innerPadding)
            ) {
                appNavGraph()
            }
        }
    )
}
