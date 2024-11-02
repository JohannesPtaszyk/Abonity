package dev.pott.abonity.app.widget.updater

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.core.domain.subscription.SubscriptionWidgetUpdater

@Module
@InstallIn(SingletonComponent::class)
interface UpdaterModule {

    @Binds
    fun bindSubscriptionWidgetUpdater(
        impl: AndroidSubscriptionWidgetUpdater,
    ): SubscriptionWidgetUpdater
}
