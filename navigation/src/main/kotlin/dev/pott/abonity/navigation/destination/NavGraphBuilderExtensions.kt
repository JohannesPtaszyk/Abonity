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
import kotlinx.collections.immutable.persistentListOf

typealias Enter = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
typealias Exit = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?

fun NavGraphBuilder.composable(
    destination: Destination,
    enterTransition: (@JvmSuppressWildcards Enter)? = null,
    exitTransition: (@JvmSuppressWildcards Exit)? = null,
    popEnterTransition: (@JvmSuppressWildcards Enter)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards Exit)? = exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = destination.route,
        arguments = if (destination is ArgumentDestination<*>) {
            destination.requiredArguments + destination.optionalArguments
        } else {
            persistentListOf()
        },
        deepLinks = if (destination is Deeplinkable) {
            destination.deeplinks
        } else {
            persistentListOf()
        },
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content,
    )
}

fun NavGraphBuilder.navigation(
    destination: Destination,
    startDestination: Destination,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(
        startDestination = startDestination.route,
        route = destination.route,
        builder = builder,
    )
}
