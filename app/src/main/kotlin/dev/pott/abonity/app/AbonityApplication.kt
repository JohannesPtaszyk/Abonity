package dev.pott.abonity.app

import android.app.Application
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AbonityApplication : Application() {
    override fun onCreate() {
        Logger.setLogWriters(LogcatWriter())
        super.onCreate()
    }
}
