package dev.pott.abonity.feature.settings.main

import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.settings.SettingsNavigationDestination
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.settingsNavGraph(
    openOssLicenses: () -> Unit,
    openNotificationSettings: () -> Unit,
) {
    navigation(
        destination = SettingsNavigationDestination,
        startDestination = SettingsScreenDestination,
    ) {
        composable(SettingsScreenDestination) {
            SettingsScreen(
                openOssLicenses = openOssLicenses,
                openNotificationSettings = openNotificationSettings,
            )
        }
    }
}
