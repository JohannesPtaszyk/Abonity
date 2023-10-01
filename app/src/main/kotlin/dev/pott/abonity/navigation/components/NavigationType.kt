package dev.pott.abonity.navigation.components

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

enum class NavigationType {
    DRAWER, RAIL, BOTTOM
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberNavigationType(activity: Activity): State<NavigationType> {
    val windowSizeClass = calculateWindowSizeClass(activity)
    return remember(windowSizeClass) {
        derivedStateOf {
            when {
                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact -> {
                    NavigationType.DRAWER
                }

                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> {
                    NavigationType.RAIL
                }

                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact -> {
                    NavigationType.BOTTOM
                }

                else -> {
                    NavigationType.RAIL
                }
            }
        }
    }
}