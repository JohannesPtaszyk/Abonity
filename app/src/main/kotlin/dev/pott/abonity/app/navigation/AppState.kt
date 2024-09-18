package dev.pott.abonity.app.navigation

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.pott.abonity.feature.home.DashboardDestination
import dev.pott.abonity.feature.subscription.SubscriptionGraphState
import dev.pott.abonity.feature.subscription.overview.OverviewDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

data class AppState(
    val navigationType: NavigationType,
    val selectedNavigationItem: NavigationItem,
    val navigationItems: ImmutableList<NavigationItem>,
    val subscriptionGraphState: SubscriptionGraphState,
    val shouldShowAddFloatingActionbutton: Boolean,
)

@Suppress("SpreadOperator")
@Composable
fun rememberAppState(navController: NavController, windowSizeClass: WindowSizeClass): AppState {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationItems = remember { NavigationItem.entries.toPersistentList() }
    val selectedNavigationItem = remember(navBackStackEntry) {
        navigationItems.find { navigationItem ->
            navBackStackEntry?.destination
                ?.hierarchy
                ?.any { it.hasRoute(navigationItem.destination) }
                ?: false
        }
    }
    return remember(selectedNavigationItem, navBackStackEntry) {
        val route = navBackStackEntry?.destination
        val selected = selectedNavigationItem ?: NavigationItem.HOME
        val navigationSuiteType = calculateFromAdaptiveInfo(windowSizeClass)
        val subscriptionGraphState = calculateSubscriptionGraphState(windowSizeClass)
        AppState(
            navigationSuiteType,
            selected,
            navigationItems,
            subscriptionGraphState,
            showAddFab(navigationSuiteType, route),
        )
    }
}

private fun showAddFab(navigationSuiteType: NavigationType, route: NavDestination?): Boolean =
    navigationSuiteType == NavigationType.NAVIGATION_BAR &&
        route != null &&
        (route.hasRoute<DashboardDestination>() || route.hasRoute<OverviewDestination>())

private fun calculateFromAdaptiveInfo(adaptiveInfo: WindowSizeClass): NavigationType =
    with(adaptiveInfo) {
        if (showNavigationBar()) {
            NavigationType.NAVIGATION_BAR
        } else {
            NavigationType.NAVIGATION_RAIL
        }
    }

private fun WindowSizeClass.showNavigationBar(): Boolean =
    heightSizeClass != WindowHeightSizeClass.Compact &&
        widthSizeClass == WindowWidthSizeClass.Compact

private fun calculateSubscriptionGraphState(
    windowSizeClass: WindowSizeClass,
): SubscriptionGraphState {
    val twoPane = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    return SubscriptionGraphState(
        showOverviewAsMultiColumn = twoPane,
    )
}
