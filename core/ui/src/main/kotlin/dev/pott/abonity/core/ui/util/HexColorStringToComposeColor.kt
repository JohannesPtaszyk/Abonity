package dev.pott.abonity.core.ui.util

import androidx.compose.ui.graphics.Color
import dev.pott.abonity.core.entity.HexColor

fun HexColor.toColor(): Color {
    return Color(android.graphics.Color.parseColor(hexValue))
}
