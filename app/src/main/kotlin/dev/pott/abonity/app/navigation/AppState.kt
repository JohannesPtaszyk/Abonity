package dev.pott.abonity.app.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.pott.abonity.feature.subscription.SubscriptionGraphState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
data class AppState(
    val navigationType: NavigationSuiteType,
    val selectedNavigationItem: NavigationItem,
    val navigationItems: ImmutableList<NavigationItem>,
    val subscriptionGraphState: SubscriptionGraphState,
)

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
)
@Suppress("SpreadOperator")
@Composable
fun rememberAppState(navController: NavController): State<AppState> {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationItems = remember { NavigationItem.values() }
    val selectedNavigationItem by remember(navBackStackEntry?.destination) {
        derivedStateOf {
            navigationItems.find { navigationItem ->
                navBackStackEntry?.destination?.hierarchy?.find {
                    navigationItem.destination.route == it.route
                } != null
            }
        }
    }
    return remember(selectedNavigationItem) {
        derivedStateOf {
            val selected = selectedNavigationItem ?: NavigationItem.SUBSCRIPTION
            val items = persistentListOf(*navigationItems)
            AppState(
                calculateFromAdaptiveInfo(adaptiveInfo),
                selected,
                items,
                getSubscriptionGraphState(adaptiveInfo.windowSizeClass),
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
)
private fun calculateFromAdaptiveInfo(adaptiveInfo: WindowAdaptiveInfo): NavigationSuiteType {
    return with(adaptiveInfo) {
        if (showNavigationBar()) {
            NavigationSuiteType.NavigationBar
        } else if (showDrawer()) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteType.NavigationRail
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun WindowAdaptiveInfo.showNavigationBar(): Boolean {
    return windowPosture.isTabletop ||
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Medium
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun WindowAdaptiveInfo.showDrawer(): Boolean {
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded
}

private fun getSubscriptionGraphState(windowSizeClass: WindowSizeClass): SubscriptionGraphState {
    val twoPane = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    return SubscriptionGraphState(
        showOverviewAsMultiColumn = twoPane,
    )
}
