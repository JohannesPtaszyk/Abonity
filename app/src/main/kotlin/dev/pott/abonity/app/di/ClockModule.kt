package dev.pott.abonity.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.db.AppDatabase
import dev.pott.abonity.core.local.db.SubscriptionDao
import kotlinx.datetime.Clock

@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    fun provideClock(): Clock {
        return Clock.System
    }
}