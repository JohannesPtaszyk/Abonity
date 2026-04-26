package dev.pott.abonity.app.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pott.abonity.core.domain.subscription.SubscriptionNotificationScheduler
import javax.inject.Inject

internal const val SUBSCRIPTION_NOTIFICATION_IMMEDIATE_WORK_ID = "SUBSCRIPTION_NOTIFICATION_NOW"

class WorkManagerSubscriptionNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : SubscriptionNotificationScheduler {

    override fun scheduleImmediateCheck() {
        val request = OneTimeWorkRequestBuilder<SubscriptionNotificationWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            SUBSCRIPTION_NOTIFICATION_IMMEDIATE_WORK_ID,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
