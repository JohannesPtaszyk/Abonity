package dev.pott.abonity.app.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
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
import dev.pott.abonity.core.entity.subscription.Subscription
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import dev.pott.abonity.core.ui.R as UiR

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
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        sendNotificationForAllSubscriptions()

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun sendNotificationForAllSubscriptions() {
        val todayEpochDays = clock.todayIn(TimeZone.currentSystemDefault()).toEpochDays()
        val subscriptions = repository.getSubscriptionsFlow().firstOrNull() ?: return
        subscriptions.forEach { subscription ->
            val config = subscription.notificationConfig ?: return@forEach
            val days = daysUntilNextPayment(subscription, todayEpochDays, calculator) ?: return@forEach
            if (days == config.daysBeforePayment) {
                sendNotification(
                    subscriptionId = subscription.id.value,
                    subscriptionName = subscription.name,
                    daysUntilPayment = days,
                )
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(
        subscriptionId: Long,
        subscriptionName: String,
        daysUntilPayment: Int,
    ) {
        val title = appContext.getString(
            UiR.string.subscription_notification_title,
            subscriptionName,
        )
        val text = if (daysUntilPayment == 0) {
            appContext.getString(UiR.string.subscription_notification_text_today, subscriptionName)
        } else {
            appContext.resources.getQuantityString(
                UiR.plurals.subscription_notification_text_days,
                daysUntilPayment,
                subscriptionName,
                daysUntilPayment,
            )
        }
        val notificationManager = NotificationManagerCompat.from(appContext)
        if (!notificationManager.areNotificationsEnabled()) return
        val notification = NotificationCompat.Builder(
            appContext,
            SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(subscriptionId.hashCode(), notification)
    }
}

/**
 * Returns the number of days until the next payment for [subscription] relative to [todayEpochDays],
 * or null if the subscription has no notification config.
 * A result of 0 means the payment is due today.
 */
internal fun daysUntilNextPayment(
    subscription: Subscription,
    todayEpochDays: Long,
    calculator: PaymentInfoCalculator,
): Int? {
    subscription.notificationConfig ?: return null
    val paymentType = subscription.paymentInfo.type
    val nextPayment = if (paymentType is PaymentType.Periodic) {
        var candidate = subscription.paymentInfo.firstPayment
        while (candidate.toEpochDays() < todayEpochDays) {
            candidate = calculator.getNextDateByType(paymentType, candidate)
        }
        candidate
    } else {
        subscription.paymentInfo.firstPayment
    }
    return (nextPayment.toEpochDays() - todayEpochDays).toInt()
}
