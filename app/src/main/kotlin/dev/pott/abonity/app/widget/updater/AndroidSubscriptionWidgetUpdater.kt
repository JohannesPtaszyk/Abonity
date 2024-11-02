package dev.pott.abonity.app.widget.updater

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pott.abonity.app.widget.payments.PaymentsWidget
import dev.pott.abonity.core.domain.subscription.SubscriptionWidgetUpdater
import javax.inject.Inject

class AndroidSubscriptionWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) : SubscriptionWidgetUpdater {
    override suspend fun update() {
        PaymentsWidget().updateAll(context)
    }
}
