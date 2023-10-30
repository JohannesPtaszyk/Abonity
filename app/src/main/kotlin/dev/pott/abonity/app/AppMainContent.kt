package dev.pott.abonity.app

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.pott.abonity.app.navigation.AppState
import dev.pott.abonity.app.navigation.NavigationItem
import dev.pott.abonity.app.navigation.appNavGraph
import dev.pott.abonity.app.navigation.components.AppBottomBar
import dev.pott.abonity.app.navigation.components.AppNavigationRail
import dev.pott.abonity.app.navigation.components.AppPermanentDrawerSheet
import dev.pott.abonity.app.navigation.components.NavigationType
import dev.pott.abonity.app.navigation.rememberAppState
import dev.pott.abonity.core.ui.theme.AppTheme

@Composable
fun AppMainContent(activity: Activity, modifier: Modifier = Modifier) {
    AppTheme {
        val navController = rememberNavController()
        val state by rememberAppState(activity, navController)
        Scaffold(
            modifier = modifier,
            bottomBar = {
                AnimatedVisibility(
                    state.navigationType == NavigationType.BOTTOM,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                ) {
                    AppBottomBar(
                        state.navigationItems,
                        state.selectedNavigationItem,
                        navController,
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            AppMainScaffoldContent(
                state,
                navController,
                innerPadding,
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun AppMainScaffoldContent(
    state: AppState,
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            AnimatedContent(
                state.navigationType,
                label = "NavigationTypeTransition",
            ) { targetNavigationType ->
                when (targetNavigationType) {
                    NavigationType.DRAWER -> {
                        AppPermanentDrawerSheet(
                            state.navigationItems,
                            state.selectedNavigationItem,
                            navController,
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 16.dp,
                            ),
                        )
                    }

                    NavigationType.RAIL -> {
                        AppNavigationRail(
                            state.navigationItems,
                            state.selectedNavigationItem,
                            navController,
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 16.dp,
                            ),
                        )
                    }

                    NavigationType.BOTTOM -> {
                        // Bottom bar will be rendered in scaffold
                    }
                }
            }
        },
        content = {
            val startRoute =
                remember {
                    NavigationItem.entries.first().destination.route
                }
            NavHost(
                navController = navController,
                startDestination = startRoute,
                Modifier.padding(innerPadding),
            ) {
                appNavGraph(state, navController)
            }
        },
    )
}
