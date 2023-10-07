package dev.pott.abonity.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.pott.abonity.app.R
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.feature.settings.SettingsScreenDestination
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.Destination

enum class NavigationItem(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val destination: Destination<*>
) {
    SUBSCRIPTION(
        R.string.navigation_item_subscription,
        dev.pott.abonity.core.ui.theme.AppIcons.Home,
        SubscriptionNavigationDestination
    ),
    SETTINGS(
        R.string.navigation_item_settings,
        dev.pott.abonity.core.ui.theme.AppIcons.Settings,
        SettingsScreenDestination
    )
}
