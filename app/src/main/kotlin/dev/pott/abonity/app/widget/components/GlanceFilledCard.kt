package dev.pott.abonity.app.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import dev.pott.abonity.app.R
import dev.pott.abonity.app.widget.theme.LocalGlanceContentColor

@Composable
fun GlanceFilledCard(
    modifier: GlanceModifier = GlanceModifier,
    onClick: Action? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                imageProvider = ImageProvider(R.drawable.glance_rounded_corner_16),
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.secondaryContainer,
                ),
            )
            .cornerRadius(16.dp)
            .then(
                if (onClick != null) {
                    GlanceModifier.clickable(onClick)
                } else {
                    GlanceModifier
                },
            ),
    ) {
        CompositionLocalProvider(
            value = LocalGlanceContentColor provides GlanceTheme.colors.onSecondaryContainer,
        ) {
            Column(content = content)
        }
    }
}
