package dev.pott.abonity.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@Composable
fun getDefaultLocale(): Locale {
    return LocalConfiguration.current.let { configuration ->
        ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
    }
}
