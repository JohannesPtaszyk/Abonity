package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateTabItem(item: NavigationItem, isSelected: Boolean = false) {
    navigate(item.destination.route) {
        val startDestination = graph.findStartDestination()
        popUpTo(startDestination.route!!) {
            saveState = !isSelected
        }
        launchSingleTop = true
        restoreState = !isSelected
    }
}
