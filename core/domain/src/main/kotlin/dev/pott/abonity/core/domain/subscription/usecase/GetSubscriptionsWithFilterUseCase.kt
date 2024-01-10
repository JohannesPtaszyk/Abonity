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

    operator fun invoke(
        selectedFilterItemsFlow: Flow<List<SubscriptionFilterItem>>,
    ): Flow<SubscriptionsWithFilter> {
        return combine(
            getSubscription().map { subscriptions ->
                val totalPrices = calculator.getTotalPricesForPeriod(
                    subscriptions.map { it.subscription.paymentInfo },
                    PaymentPeriod.MONTHS,
                )
                val currencyFilterItems = totalPrices.map { SubscriptionFilterItem.Currency(it) }
                subscriptions to currencyFilterItems
            },
            settingsRepository.getSettingsFlow().map { it.period },
            selectedFilterItemsFlow,
        ) { (subscriptions, currencyFilterItems), period, selectedFilterItems ->
            val filteredSubscriptions = if (selectedFilterItems.isEmpty()) {
                subscriptions
            } else {
                applySelectedFilter(period, subscriptions, selectedFilterItems)
            }
            SubscriptionsWithFilter(
                filteredSubscriptions,
                SubscriptionFilter(
                    buildList {
                        add(SubscriptionFilterItem.CurrentPeriod(period))
                        addAll(currencyFilterItems)
                    }.sortedBy { !selectedFilterItems.contains(it) },
                    selectedFilterItems,
                ),
            )
        }.flowOn(defaultDispatcher)
    }

    private fun applySelectedFilter(
        period: PaymentPeriod,
        subscriptions: List<SubscriptionWithPeriodInfo>,
        selectedFilterItems: List<SubscriptionFilterItem>,
    ): List<SubscriptionWithPeriodInfo> {
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        val from = today.getFirstDayOfCurrentPeriod(period)
        val to = today.getLastDayOfCurrentPeriod(period)
        return subscriptions.filter { subscriptionWithPeriodInfo ->
            val appliedFilter = selectedFilterItems.map { filterItem ->
                when (filterItem) {
                    is SubscriptionFilterItem.Currency -> {
                        val subscription = subscriptionWithPeriodInfo.subscription
                        subscription.paymentInfo.price.currency == filterItem.price.currency
                    }

                    is SubscriptionFilterItem.CurrentPeriod -> {
                        subscriptionWithPeriodInfo.nextPaymentDate in from..to
                    }
                }
            }
            appliedFilter.any { it }
        }
    }
}
