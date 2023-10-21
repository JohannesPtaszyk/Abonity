package dev.pott.abonity.core.ui.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import java.lang.IllegalStateException

fun Context.getActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> throw IllegalStateException("Current Activity could not be found")
}
