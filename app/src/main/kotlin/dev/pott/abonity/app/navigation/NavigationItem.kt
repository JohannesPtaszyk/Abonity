package dev.pott.abonity.app.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.feature.home.HomeNavigationDestination
import dev.pott.abonity.feature.settings.SettingsNavigationDestination
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.Destination

enum class NavigationItem(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val destination: Destination,
) {
    HOME(
        R.string.navigation_item_home,
        AppIcons.Home,
        HomeNavigationDestination,
    ),
    SUBSCRIPTION(
        R.string.navigation_item_subscription,
        AppIcons.CreditCard,
        SubscriptionNavigationDestination,
    ),
    SETTINGS(
        R.string.navigation_item_settings,
        AppIcons.Settings,
        SettingsNavigationDestination,
    ),
}
