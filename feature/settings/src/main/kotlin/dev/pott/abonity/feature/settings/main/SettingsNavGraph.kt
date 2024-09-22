package dev.pott.abonity.feature.settings.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.pott.abonity.feature.settings.SettingsNavigationDestination

fun NavGraphBuilder.settingsNavGraph(
    openOssLicenses: () -> Unit,
    openNotificationSettings: () -> Unit,
    openConsentDialog: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit = {},
) {
    navigation<SettingsNavigationDestination>(SettingsDestination) {
        composable<SettingsDestination> {
            SettingsScreen(
                openOssLicenses = openOssLicenses,
                openNotificationSettings = openNotificationSettings,
                openConsentDialog = openConsentDialog,
            )
        }
        nestedGraphs()
    }
}
