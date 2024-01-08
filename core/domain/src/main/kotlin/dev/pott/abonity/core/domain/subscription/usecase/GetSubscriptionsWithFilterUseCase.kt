package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.common.injection.Dispatcher
import dev.pott.abonity.common.injection.Dispatcher.Type.DEFAULT
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.getFirstDayOfCurrentPeriod
import dev.pott.abonity.core.domain.subscription.getLastDayOfCurrentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.entity.subscription.SubscriptionsWithFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetSubscriptionsWithFilterUseCase @Inject constructor(
    private val getSubscription: GetSubscriptionsWithPeriodPrice,
    private val settingsRepository: SettingsRepository,
    private val calculator: PaymentInfoCalculator,
    private val clock: Clock,
    @Dispatcher(DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) {

    private var cachedSubscriptionWithFilter: SubscriptionsWithFilter? = null

    operator fun invoke(
        selectedFilterItems: List<SubscriptionFilterItem>,
    ): Flow<SubscriptionsWithFilter> {
        return flow {
            cachedSubscriptionWithFilter?.let { cached ->
                val updatedFilter = cached.filter.copy(selectedItems = selectedFilterItems)
                emit(cached.copy(filter = updatedFilter))
            }

            combine(
                getSubscription(),
                settingsRepository.getSettingsFlow(),
            ) { subscriptions, settings ->
                subscriptions to settings
            }.map { (subscriptions, settings) ->
                val totalPrices = calculator.getTotalPricesForPeriod(
                    subscriptions.map { it.subscription.paymentInfo },
                    PaymentPeriod.MONTHS,
                )
                val filteredSubscriptions = if (selectedFilterItems.isEmpty()) {
                    subscriptions
                } else {
                    applySelectedFilter(subscriptions, selectedFilterItems)
                }

                SubscriptionsWithFilter(
                    filteredSubscriptions,
                    SubscriptionFilter(totalPrices, settings.period, selectedFilterItems),
                ).also {
                    cachedSubscriptionWithFilter = it
                }
            }.also {
                emitAll(it)
            }
        }.flowOn(defaultDispatcher)
    }

    private fun applySelectedFilter(
        subscriptions: List<SubscriptionWithPeriodInfo>,
        selectedFilterItems: List<SubscriptionFilterItem>,
    ) = subscriptions.filter { subscriptionWithPeriodInfo ->
        val appliedFilter = selectedFilterItems.map { filterItem ->
            when (filterItem) {
                is SubscriptionFilterItem.Currency -> {
                    val subscription = subscriptionWithPeriodInfo.subscription
                    subscription.paymentInfo.price.currency == filterItem.price.currency
                }

                is SubscriptionFilterItem.CurrentPeriod -> {
                    val today = clock.todayIn(TimeZone.currentSystemDefault())
                    val from =
                        today.getFirstDayOfCurrentPeriod(PaymentPeriod.MONTHS)
                    val to = today.getLastDayOfCurrentPeriod(PaymentPeriod.MONTHS)
                    subscriptionWithPeriodInfo.nextPaymentDate in from..to
                }
            }
        }
        appliedFilter.any { it }
    }
}
