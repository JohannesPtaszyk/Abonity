package dev.pott.abonity.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.pott.abonity.R
import dev.pott.abonity.feature.settings.SettingsScreenDestination
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.Destination
import dev.pott.abonity.theme.AppIcons

enum class NavigationItem(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val destination: Destination<*>
) {
    SUBSCRIPTION(
        R.string.navigation_item_subscription,
        AppIcons.Home,
        SubscriptionNavigationDestination
    ),
    SETTINGS(
        R.string.navigation_item_settings,
        AppIcons.Settings,
        SettingsScreenDestination
    )
}