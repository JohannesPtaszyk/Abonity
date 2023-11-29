package dev.pott.abonity.navigation.destination

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import co.touchlab.kermit.Logger

typealias Enter = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
typealias Exit = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?

fun NavGraphBuilder.composable(
    destination: Destination<*>,
    enterTransition: (@JvmSuppressWildcards Enter)? = null,
    exitTransition: (@JvmSuppressWildcards Exit)? = null,
    popEnterTransition: (@JvmSuppressWildcards Enter)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards Exit)? = exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = destination.route.also {
            Logger.withTag("NavGraphBuilder.composable").i {
                "Added destination with route $it ($destination)"
            }
        },
        arguments = destination.arguments,
        deepLinks = destination.deeplinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content,
    )
}

fun NavGraphBuilder.navigation(
    destination: NavigationDestination<*>,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(
        startDestination = destination.startDestination.route,
        route = destination.route,
        builder = builder,
    )
}
