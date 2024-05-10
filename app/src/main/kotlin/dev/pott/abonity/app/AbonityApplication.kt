package dev.pott.abonity.app

import android.app.Application
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AbonityApplication : Application() {

    @Inject
    lateinit var trackingServiceManager: TrackingServiceManager

    override fun onCreate() {
        Logger.setLogWriters(LogcatWriter())
        super.onCreate()
        trackingServiceManager.init()
    }
}
