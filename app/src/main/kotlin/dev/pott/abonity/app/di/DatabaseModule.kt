@file:Suppress("MagicNumber")

package dev.pott.abonity.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.subscription.db.AppDatabase
import javax.inject.Singleton

private const val APP_DATABASE = "app-database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DATABASE,
        ).fallbackToDestructiveMigration()
            .build()
}
