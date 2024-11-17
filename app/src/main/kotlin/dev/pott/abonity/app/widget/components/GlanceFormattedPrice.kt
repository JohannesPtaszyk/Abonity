package dev.pott.abonity.app.widget.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.unit.ColorProvider
import dev.pott.abonity.app.widget.theme.LocalGlanceContentColor
import dev.pott.abonity.app.widget.theme.LocalGlanceTextStyle
import dev.pott.abonity.core.entity.subscription.Price
import java.text.NumberFormat
import java.util.Currency

@Composable
fun GlanceFormattedPrice(
    price: Price,
    modifier: GlanceModifier = GlanceModifier,
    style: androidx.glance.text.TextStyle = LocalGlanceTextStyle.current,
    color: ColorProvider = LocalGlanceContentColor.current,
    maxLines: Int = Int.MAX_VALUE,
) {
    val formatted = rememberFormattedPrice(
        value = price.value,
        currency = price.currency,
    )
    GlanceText(formatted, modifier, style, color, maxLines)
}

@Composable
private fun rememberFormattedPrice(value: Double, currency: Currency): String {
    val locale = LocalContext.current.resources.configuration.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            it.locales[0]
        } else {
            @Suppress("DEPRECATION") it.locale
        }
    }
    val format = remember(locale) {
        NumberFormat.getCurrencyInstance(locale)
    }
    return remember(value, currency) {
        format.currency = currency
        format.format(value)
    }
}
