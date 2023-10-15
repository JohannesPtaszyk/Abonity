package dev.pott.abonity.app.navigation

import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.settings.SettingsScreen
import dev.pott.abonity.feature.settings.SettingsScreenDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph
import dev.pott.abonity.navigation.destination.composable

fun NavGraphBuilder.appNavGraph(state: AppState) {
    subscriptionGraph(state.subscriptionGraphState)
    composable(SettingsScreenDestination) { SettingsScreen() }
}
