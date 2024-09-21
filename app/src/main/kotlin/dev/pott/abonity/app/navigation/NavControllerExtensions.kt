package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import dev.pott.abonity.feature.home.HomeNavigationDestination
import dev.pott.abonity.feature.settings.SettingsNavigationDestination
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination

fun NavController.navigateTabItem(item: NavigationItem, isSelected: Boolean = false) {
    val destination = when (item) {
        NavigationItem.HOME -> HomeNavigationDestination
        NavigationItem.SUBSCRIPTION -> SubscriptionNavigationDestination()
        NavigationItem.SETTINGS -> SettingsNavigationDestination
    }
    navigate(destination) {
        val startDestination = graph.findStartDestination()
        popUpTo(startDestination.route!!) {
            saveState = !isSelected
        }
        launchSingleTop = true
        restoreState = !isSelected
    }
}
