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
import dev.pott.abonity.core.local.legal.datastore.LegalEntitySerializer
import dev.pott.abonity.core.local.legal.datastore.entities.LegalEntity
import dev.pott.abonity.core.local.notification.datastore.NotificationTeaserEntitySerializer
import dev.pott.abonity.core.local.notification.datastore.entities.NotificationTeaserEntity
import dev.pott.abonity.core.local.settings.datastore.SettingsEntitySerializer
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import javax.inject.Singleton

private const val SETTINGS_FILE_NAME = "settings.json"
private const val NOTIFICATION_TEASER_FILE_NAME = "notification_teaser.json"
private const val LEGAL_FILE_NAME = "legal.json"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<SettingsEntity> =
        DataStoreFactory.create(
            serializer = SettingsEntitySerializer(),
        ) {
            context.dataStoreFile(SETTINGS_FILE_NAME)
        }

    @Provides
    @Singleton
    fun provideNotificationDataStore(
        @ApplicationContext context: Context,
    ): DataStore<NotificationTeaserEntity> =
        DataStoreFactory.create(
            serializer = NotificationTeaserEntitySerializer(),
        ) {
            context.dataStoreFile(NOTIFICATION_TEASER_FILE_NAME)
        }

    @Provides
    @Singleton
    fun provideLegalDataStore(@ApplicationContext context: Context): DataStore<LegalEntity> =
        DataStoreFactory.create(
            serializer = LegalEntitySerializer(),
        ) {
            context.dataStoreFile(LEGAL_FILE_NAME)
        }
}
