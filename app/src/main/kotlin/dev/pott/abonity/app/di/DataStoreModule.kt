package dev.pott.abonity.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.local.settings.datastore.SettingsEntitySerializer
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import javax.inject.Singleton

private const val SETTINGS_FILE_NAME = "settings.json"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<SettingsEntity> {
        return DataStoreFactory.create(
            serializer = SettingsEntitySerializer(),
        ) {
            context.dataStoreFile(SETTINGS_FILE_NAME)
        }
    }
}
