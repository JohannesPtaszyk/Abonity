package dev.pott.abonity.app.navigation.components

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

enum class NavigationType {
    DRAWER, RAIL, BOTTOM
}

@Composable
fun rememberNavigationType(windowSizeClass: WindowSizeClass): State<NavigationType> {
    return remember(windowSizeClass) {
        derivedStateOf {
            when {
                isDrawer(windowSizeClass) -> {
                    NavigationType.DRAWER
                }

                isNavigationRail(windowSizeClass) -> {
                    NavigationType.RAIL
                }

                isBottomNavigation(windowSizeClass) -> {
                    NavigationType.BOTTOM
                }

                else -> {
                    NavigationType.RAIL
                }
            }
        }
    }
}

private fun isBottomNavigation(windowSizeClass: WindowSizeClass): Boolean {
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
}

private fun isNavigationRail(windowSizeClass: WindowSizeClass): Boolean {
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium ||
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
}

private fun isDrawer(windowSizeClass: WindowSizeClass): Boolean {
    return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
        windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
}
