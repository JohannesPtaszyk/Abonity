package dev.pott.abonity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.navigation.components.BottomBarItem
import dev.pott.abonity.navigation.components.DrawerItem
import dev.pott.abonity.navigation.NavigationItem
import dev.pott.abonity.navigation.appNavGraph
import dev.pott.abonity.navigation.components.NavigationType
import dev.pott.abonity.navigation.components.RailItem
import dev.pott.abonity.navigation.components.rememberNavigationType
import dev.pott.abonity.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navigationType by rememberNavigationType(this)
                val tabs = NavigationItem.values()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            navigationType == NavigationType.BOTTOM,
                            enter = slideInVertically { it } + fadeIn(),
                            exit = slideOutVertically { it } + fadeOut()
                        ) {
                            BottomAppBar {
                                tabs.forEach {
                                    BottomBarItem(
                                        it,
                                        selected = currentDestination?.route == it.destination.route,
                                        onClick = { navController.navigateTabItem(it) }
                                    )
                                }
                            }
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
                                        PermanentDrawerSheet(
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 16.dp
                                            )
                                        ) {
                                            tabs.forEach {
                                                DrawerItem(
                                                    it,
                                                    selected = currentDestination?.route == it.destination.route,
                                                    onClick = { navController.navigateTabItem(it) }
                                                )
                                            }
                                        }
                                    }

                                    NavigationType.RAIL -> {
                                        NavigationRail(
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 16.dp
                                            )
                                        ) {
                                            tabs.forEach {
                                                RailItem(
                                                    it,
                                                    selected = currentDestination?.route == it.destination.route,
                                                    onClick = { navController.navigateTabItem(it) }
                                                )
                                            }
                                        }
                                    }

                                    NavigationType.BOTTOM -> {
                                        // No content as we show bottom bar in this case
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
    }
}

fun NavController.navigateTabItem(item: NavigationItem) {
    navigate(item.destination.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}