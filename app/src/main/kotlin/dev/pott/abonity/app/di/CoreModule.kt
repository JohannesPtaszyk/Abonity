package dev.pott.abonity.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.domain.SubscriptionLocalDataSource
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.domain.SubscriptionRepositoryImpl
import dev.pott.abonity.core.local.RoomSubscriptionDataSource

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindsSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    fun bindsSubscriptionLocalDataSource(impl: RoomSubscriptionDataSource): SubscriptionLocalDataSource
}
