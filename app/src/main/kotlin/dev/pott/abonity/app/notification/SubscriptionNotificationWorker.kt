package dev.pott.abonity.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.pott.abonity.app.R
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.ui.R as UiR
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

const val SUBSCRIPTION_NOTIFICATION_CHANNEL_ID = "subscription_payment_reminders"

@HiltWorker
class SubscriptionNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SubscriptionRepository,
    private val calculator: PaymentInfoCalculator,
    private val clock: Clock,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        ensureNotificationChannel()
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        val subscriptions = repository.getSubscriptionsFlow().firstOrNull() ?: return Result.success()
        subscriptions.forEach { subscription ->
            val config = subscription.notificationConfig ?: return@forEach
            val paymentType = subscription.paymentInfo.type
            val nextPayment = if (paymentType is PaymentType.Periodic) {
                calculator.getNextDateByType(paymentType)
            } else {
                subscription.paymentInfo.firstPayment
            }
            val daysUntilPayment = (nextPayment.toEpochDays() - today.toEpochDays()).toInt()
            if (daysUntilPayment == config.daysBeforePayment) {
                sendNotification(
                    subscriptionId = subscription.id.value.toInt(),
                    subscriptionName = subscription.name,
                    daysUntilPayment = daysUntilPayment,
                )
            }
        }
        return Result.success()
    }

    private fun ensureNotificationChannel() {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(SUBSCRIPTION_NOTIFICATION_CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
                appContext.getString(UiR.string.subscription_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(
        subscriptionId: Int,
        subscriptionName: String,
        daysUntilPayment: Int,
    ) {
        val title = appContext.getString(UiR.string.subscription_notification_title, subscriptionName)
        val text = if (daysUntilPayment == 0) {
            appContext.getString(UiR.string.subscription_notification_text_today, subscriptionName)
        } else {
            appContext.getString(
                UiR.string.subscription_notification_text_days,
                subscriptionName,
                daysUntilPayment,
            )
        }
        val notification = NotificationCompat.Builder(appContext, SUBSCRIPTION_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(appContext).notify(subscriptionId, notification)
    }
}
