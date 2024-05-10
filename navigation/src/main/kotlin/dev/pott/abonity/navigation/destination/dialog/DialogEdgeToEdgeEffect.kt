package dev.pott.abonity.navigation.destination.dialog

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun DialogEdgeToEdgeEffect() {
    val activityWindow = LocalView.current.context.findActivityWindow() ?: return
    val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window ?: return
    val parentView = LocalView.current.parent as View
    DisposableEffect(Unit) {
        val attributes = WindowManager.LayoutParams().apply {
            copyFrom(activityWindow.attributes)
            type = dialogWindow.attributes.type
        }
        dialogWindow.attributes = attributes
        parentView.layoutParams = FrameLayout.LayoutParams(
            activityWindow.decorView.width,
            activityWindow.decorView.height,
        )
        onDispose {}
    }
}

private tailrec fun Context.findActivityWindow(): Window? {
    return when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findActivityWindow()
        else -> null
    }
}
