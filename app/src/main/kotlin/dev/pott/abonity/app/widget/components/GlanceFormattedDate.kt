package dev.pott.abonity.app.widget.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.pott.abonity.app.widget.theme.LocalGlanceContentColor
import dev.pott.abonity.app.widget.theme.LocalGlanceTextStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun GlanceFormattedDate(
    date: LocalDate,
    modifier: GlanceModifier = GlanceModifier,
    style: TextStyle = LocalGlanceTextStyle.current,
    color: ColorProvider = LocalGlanceContentColor.current,
    maxLines: Int = Int.MAX_VALUE,
) {
    val formattedDate = rememberFormattedDate(date)
    GlanceText(formattedDate, modifier, style, color, maxLines)
}

@Composable
fun rememberFormattedDate(date: LocalDate): String {
    val locale = LocalContext.current.resources.configuration.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            it.locales[0]
        } else {
            @Suppress("DEPRECATION") it.locale
        }
    }
    val localizedDate = remember(locale, date) {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        date.toJavaLocalDate().format(formatter)
    }
    return localizedDate
}
