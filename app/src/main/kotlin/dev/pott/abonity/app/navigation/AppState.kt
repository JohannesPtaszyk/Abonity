package dev.pott.abonity.app.navigation

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.pott.abonity.app.navigation.components.NavigationType
import dev.pott.abonity.app.navigation.components.rememberNavigationType
import dev.pott.abonity.feature.subscription.SubscriptionGraphState

data class AppState(
    val navigationType: NavigationType,
    val selectedNavigationItem: NavigationItem,
    val navigationItems: List<NavigationItem>,
    val subscriptionGraphState: SubscriptionGraphState
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppState(
    activity: Activity,
    navController: NavController
): State<AppState> {
    val windowSizeClass = calculateWindowSizeClass(activity)
    val navigationType by rememberNavigationType(windowSizeClass)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navigationItems = NavigationItem.values()
    val selectedNavigationItem by remember(currentDestination) {
        derivedStateOf {
            navigationItems.find { navigationItem ->
                currentDestination?.hierarchy?.find {
                    navigationItem.destination.route == it.route
                } != null
            }
        }
    }
    return remember {
        derivedStateOf {
            AppState(
                navigationType,
                selectedNavigationItem ?: NavigationItem.SUBSCRIPTION,
                navigationItems.toList(),
                SubscriptionGraphState(windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium)
            )
        }
    }
}
