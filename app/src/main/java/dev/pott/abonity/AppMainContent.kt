package dev.pott.abonity

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.appNavGraph
import dev.pott.abonity.navigation.components.AppBottomBar
import dev.pott.abonity.navigation.components.AppNavigationRail
import dev.pott.abonity.navigation.components.AppPermanentDrawerSheet
import dev.pott.abonity.navigation.components.NavigationType
import dev.pott.abonity.navigation.components.rememberNavigationType
import dev.pott.abonity.theme.AppTheme

@Composable
fun AppMainContent(activity: Activity) {
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
            PermanentNavigationDrawer(
                drawerContent = {
                    AnimatedContent(
                        navigationType,
                        label = "NavigationTypeTransition"
                    ) { targetNavigationType ->
                        when (targetNavigationType) {
                            NavigationType.DRAWER -> {
                                AppPermanentDrawerSheet(tabs, selectedTab, navController)
                            }

                            NavigationType.RAIL -> {
                                AppNavigationRail(tabs, selectedTab, navController)
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
                        appNavGraph(navController)
                    }
                }
            )
        }
    }
}