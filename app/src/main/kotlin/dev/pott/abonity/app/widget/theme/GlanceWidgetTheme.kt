package dev.pott.abonity.app.widget.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import androidx.glance.text.TextDefaults.defaultTextStyle
import dev.pott.abonity.core.ui.theme.darkScheme
import dev.pott.abonity.core.ui.theme.lightScheme

private val colors = ColorProviders(
    light = lightScheme,
    dark = darkScheme,
)

@Composable
fun GlanceWidgetTheme(content: @Composable @GlanceComposable () -> Unit) {
    val sdkDependentColors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        GlanceTheme.colors
    } else {
        colors
    }
    GlanceTheme(colors = sdkDependentColors) {
        CompositionLocalProvider(
            LocalGlanceContentColor provides sdkDependentColors.onBackground,
            LocalGlanceTextStyle provides GlanceTypography.body,
        ) {
            content()
        }
    }
}

@Suppress("CompositionLocalAllowlist")
val LocalGlanceContentColor = compositionLocalOf { colors.onBackground }

@Suppress("CompositionLocalAllowlist")
val LocalGlanceTextStyle = compositionLocalOf { defaultTextStyle }
