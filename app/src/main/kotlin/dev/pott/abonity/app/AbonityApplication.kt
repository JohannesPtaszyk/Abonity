package dev.pott.abonity.app

import android.app.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AbonityApplication : Application() {

    @Inject
    lateinit var trackingServiceManager: TrackingServiceManager

    @OptIn(ExperimentalKermitApi::class)
    override fun onCreate() {
        Logger.setLogWriters(LogcatWriter(), CrashlyticsLogWriter())
        super.onCreate()
        trackingServiceManager.init()
    }
}
