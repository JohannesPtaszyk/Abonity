package dev.pott.abonity.app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainActivityViewModel>()

    @ExperimentalMaterial3WindowSizeClassApi
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var mainState: MainState by mutableStateOf(MainState.Loading)

        splashScreen.setKeepOnScreenCondition {
            when (mainState) {
                MainState.Loading -> true
                is MainState.Success -> false
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collect {
                    mainState = it
                }
            }
        }

        enableEdgeToEdge()

        setContent {
            val isDarkTheme = shouldUseDarkTheme(mainState)

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            val windowSizeClass = calculateWindowSizeClass(activity = this)
            AppTheme(isDarkTheme, shouldUseSystemTheme(state = mainState)) {
                App(
                    windowSizeClass = windowSizeClass,
                    openNotificationSettings = {
                        val intent = Intent()
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.setData(Uri.fromParts("package", packageName, null))
                        } else {
                            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        startActivity(intent)
                    },
                )
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(state: MainState): Boolean =
    when (state) {
        MainState.Loading -> isSystemInDarkTheme()
        is MainState.Success -> when (state.theme) {
            Theme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            Theme.LIGHT -> false
            Theme.DARK -> true
        }
    }

@Composable
private fun shouldUseSystemTheme(state: MainState): Boolean =
    when (state) {
        MainState.Loading -> true
        is MainState.Success -> state.adaptiveColorsEnabled
    }

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
