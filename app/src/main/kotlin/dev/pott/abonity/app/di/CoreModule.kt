package dev.pott.abonity.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.app.firebase.FirebaseRemoteConfigDataSource
import dev.pott.abonity.core.domain.config.ConfigRepository
import dev.pott.abonity.core.domain.config.ConfigRepositoryImpl
import dev.pott.abonity.core.domain.config.LocalConfigDataSource
import dev.pott.abonity.core.domain.config.RemoteConfigDataSource
import dev.pott.abonity.core.domain.legal.LegalLocalDataSource
import dev.pott.abonity.core.domain.legal.LegalRepository
import dev.pott.abonity.core.domain.legal.LegalRepositoryImpl
import dev.pott.abonity.core.domain.notification.NotificationTeaserLocalDataSource
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepositoryImpl
import dev.pott.abonity.core.domain.settings.SettingsLocalDataSource
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.settings.SettingsRepositoryImpl
import dev.pott.abonity.core.domain.subscription.CategoryLocalDataSource
import dev.pott.abonity.core.domain.subscription.CategoryRepository
import dev.pott.abonity.core.domain.subscription.CategoryRepositoryImpl
import dev.pott.abonity.core.domain.subscription.SubscriptionLocalDataSource
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.domain.subscription.SubscriptionRepositoryImpl
import dev.pott.abonity.core.local.legal.LegalDataStoreDataSource
import dev.pott.abonity.core.local.notification.NotificationTeaserDataStoreDataSource
import dev.pott.abonity.core.local.settings.SettingsDataStoreDataSource
import dev.pott.abonity.core.local.subscription.RoomCategoryDataSource
import dev.pott.abonity.core.local.subscription.RoomSubscriptionDataSource

@Suppress("TooManyFunctions")
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
    fun bindsCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    fun bindsCategoryLocalDataSource(impl: RoomCategoryDataSource): CategoryLocalDataSource

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

    @Binds
    fun bindLegalRepository(impl: LegalRepositoryImpl): LegalRepository

    @Binds
    fun bindLegalLocalDataSource(impl: LegalDataStoreDataSource): LegalLocalDataSource

    @Binds
    fun bindConfigRepository(impl: ConfigRepositoryImpl): ConfigRepository

    @Binds
    fun bindRemoteConfigDataSource(impl: FirebaseRemoteConfigDataSource): RemoteConfigDataSource

    @Binds
    fun bindLocalConfigDataSource(impl: FirebaseRemoteConfigDataSource): LocalConfigDataSource
}
