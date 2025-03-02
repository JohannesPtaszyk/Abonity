package dev.pott.abonity.app

import android.app.Application
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
import dev.pott.abonity.app.widget.work.SubscriptionWidgetUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

private const val SUBSCRIPTION_WIDGET_WORK_ID = "SUBSCRIPTION_WIDGET_UPDATE"

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
        Logger.setLogWriters(LogcatWriter(), CrashlyticsLogWriter())
        super.onCreate()
        trackingServiceManager.init()
        scope.launch {
            val now = clock.now()
            val timeZone = TimeZone.currentSystemDefault()
            val startOfNextDay = now
                .plus(1.days)
                .toLocalDateTime(timeZone)
                .let {
                    LocalDateTime(
                        it.year,
                        it.monthNumber,
                        it.dayOfMonth,
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
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}
