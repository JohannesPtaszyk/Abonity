package dev.pott.abonity.app.widget.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import co.touchlab.kermit.Logger

class LaunchDeeplinkAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        Logger.withTag("PaymentsWidget").d("Launching deeplink: ${parameters[deeplinkKey]}")
        val deeplink = parameters[deeplinkKey] ?: return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
            setPackage(context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    companion object {
        val deeplinkKey = ActionParameters.Key<String>("deeplink")
    }
}
