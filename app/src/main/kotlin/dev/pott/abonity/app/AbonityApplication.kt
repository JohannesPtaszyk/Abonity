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
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.app.widget.work.SubscriptionWidgetUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

private const val SUBSCRIPTION_WIDGET_WORK_ID = "SUBSCRIPTION_WIDGET_UPDATE"

@HiltAndroidApp
class AbonityApplication :
    Application(),
    Configuration.Provider {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

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
            val beginningOfNextDayMillis = clock.now()
                .plus(1.days)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()

            val workRequestBuilder = PeriodicWorkRequestBuilder<SubscriptionWidgetUpdateWorker>(
                1L,
                TimeUnit.DAYS,
            )
            val workRequest = workRequestBuilder
                .setNextScheduleTimeOverride(beginningOfNextDayMillis)
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

    override val workManagerConfiguration: Configuration =
        Configuration.Builder().setWorkerFactory(
            EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory(),
        ).build()
}
