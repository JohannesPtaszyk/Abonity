package dev.pott.abonity.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.settings.SettingsScreen
import dev.pott.abonity.feature.settings.SettingsScreenDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph
import dev.pott.abonity.navigation.destination.composable

fun NavGraphBuilder.appNavGraph(navController: NavController) {
    subscriptionGraph(navController)
    composable(SettingsScreenDestination) { SettingsScreen() }
}