package dev.pott.abonity.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dagger.hilt.android.HiltAndroidApp
import dev.pott.abonity.app.firebase.setFirebaseDefaultCustomKeys
import dev.pott.abonity.app.notification.SUBSCRIPTION_NOTIFICATION_CHANNEL_ID
import dev.pott.abonity.app.notification.SubscriptionNotificationWorker
import dev.pott.abonity.app.widget.work.SubscriptionWidgetUpdateWorker
import dev.pott.abonity.core.ui.R as UiR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

private const val SUBSCRIPTION_WIDGET_WORK_ID = "SUBSCRIPTION_WIDGET_UPDATE"
private const val SUBSCRIPTION_NOTIFICATION_WORK_ID = "SUBSCRIPTION_NOTIFICATION"
private const val NOTIFICATION_WORK_HOUR = 8

@HiltAndroidApp
class AbonityApplication :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var trackingServiceManager: TrackingServiceManager

    @Inject
    lateinit var clock: Clock

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    @OptIn(ExperimentalKermitApi::class)
    override fun onCreate() {
        setFirebaseDefaultCustomKeys()
        Logger.setLogWriters(LogcatWriter(), CrashlyticsLogWriter())
        super.onCreate()
        trackingServiceManager.init()
        createNotificationChannel()
        scope.launch {
            val now = clock.now()
            val timeZone = TimeZone.currentSystemDefault()
            val startOfNextDay = now
                .plus(1.days)
                .toLocalDateTime(timeZone)
                .let {
                    LocalDateTime(
                        it.year,
                        it.month,
                        it.day,
                        0,
                        0,
                        0,
                    ).toInstant(timeZone)
                }.toEpochMilliseconds()

            val workRequestBuilder = PeriodicWorkRequestBuilder<SubscriptionWidgetUpdateWorker>(
                1L,
                TimeUnit.DAYS,
            )
            val workRequest = workRequestBuilder
                .setNextScheduleTimeOverride(startOfNextDay)
                .build()

            WorkManager
                .getInstance(this@AbonityApplication)
                .enqueueUniquePeriodicWork(
                    SUBSCRIPTION_WIDGET_WORK_ID,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest,
                )

            val nowMillis = now.toEpochMilliseconds()
            val notificationStartTime = now
                .toLocalDateTime(timeZone)
                .let { currentDateTime ->
                    val todayAt8 = LocalDateTime(
                        currentDateTime.year,
                        currentDateTime.month,
                        currentDateTime.day,
                        NOTIFICATION_WORK_HOUR,
                        0,
                        0,
                    ).toInstant(timeZone)
                    val todayAt8Millis = todayAt8.toEpochMilliseconds()
                    if (todayAt8Millis > nowMillis) todayAt8Millis else todayAt8.plus(1.days).toEpochMilliseconds()
                }

            val notificationWorkRequest =
                PeriodicWorkRequestBuilder<SubscriptionNotificationWorker>(1L, TimeUnit.DAYS)
                    .setNextScheduleTimeOverride(notificationStartTime)
                    .build()

            WorkManager
                .getInstance(this@AbonityApplication)
                .enqueueUniquePeriodicWork(
                    SUBSCRIPTION_NOTIFICATION_WORK_ID,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    notificationWorkRequest,
                )
        }
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(SUBSCRIPTION_NOTIFICATION_CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                SUBSCRIPTION_NOTIFICATION_CHANNEL_ID,
                getString(UiR.string.subscription_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(channel)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}
