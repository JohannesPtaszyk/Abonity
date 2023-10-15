package dev.pott.abonity.core.ui.util

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color = Color(android.graphics.Color.parseColor(this))
