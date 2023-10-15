package dev.pott.abonity.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.db.AppDatabase
import dev.pott.abonity.core.local.db.SubscriptionDao

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideSubscriptionDao(appDatabase: AppDatabase): SubscriptionDao {
        return appDatabase.subscriptionDao()
    }
}
