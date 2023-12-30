package dev.pott.abonity.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.domain.notification.NotificationTeaserLocalDataSource
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepositoryImpl
import dev.pott.abonity.core.domain.settings.SettingsLocalDataSource
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.settings.SettingsRepositoryImpl
import dev.pott.abonity.core.domain.subscription.SubscriptionLocalDataSource
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.domain.subscription.SubscriptionRepositoryImpl
import dev.pott.abonity.core.local.notification.NotificationTeaserDataStoreDataSource
import dev.pott.abonity.core.local.settings.SettingsDataStoreDataSource
import dev.pott.abonity.core.local.subscription.RoomSubscriptionDataSource

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {
    @Binds
    fun bindsSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    fun bindsSubscriptionLocalDataSource(
        impl: RoomSubscriptionDataSource,
    ): SubscriptionLocalDataSource

    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindSettingsLocalDataSource(impl: SettingsDataStoreDataSource): SettingsLocalDataSource

    @Binds
    fun bindNotificationTeaserRepository(
        impl: NotificationTeaserRepositoryImpl,
    ): NotificationTeaserRepository

    @Binds
    fun bindNotificationTeaserLocalDataSource(
        impl: NotificationTeaserDataStoreDataSource,
    ): NotificationTeaserLocalDataSource
}
