package dev.pott.abonity.app.widget.components

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.pott.abonity.app.widget.theme.LocalGlanceContentColor
import dev.pott.abonity.app.widget.theme.LocalGlanceTextStyle

@Composable
fun GlanceText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    style: TextStyle = LocalGlanceTextStyle.current,
    color: ColorProvider = LocalGlanceContentColor.current,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(text, modifier, style.copy(color = color), maxLines)
}
