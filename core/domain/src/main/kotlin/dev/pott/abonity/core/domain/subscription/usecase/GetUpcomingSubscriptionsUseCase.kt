package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.common.injection.Dispatcher
import dev.pott.abonity.common.injection.Dispatcher.Type.DEFAULT
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.subscription.getLastDayOfCurrentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetUpcomingSubscriptionsUseCase @Inject constructor(
    private val clock: Clock,
    private val getSubscriptionsWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
    private val settingsRepository: SettingsRepository,
    @Dispatcher(DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(): Flow<List<SubscriptionWithPeriodInfo>> {
        return combine(
            getSubscriptionsWithPeriodPrice(),
            settingsRepository.getSettingsFlow(),
        ) { subscriptions, settings ->
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            subscriptions.filter {
                val lastDayOfCurrentPeriod = today.getLastDayOfCurrentPeriod(settings.period)
                it.nextPaymentDate in today..lastDayOfCurrentPeriod
            }
        }.flowOn(defaultDispatcher)
    }
}
