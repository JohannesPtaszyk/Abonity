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
    getSubscription: GetSubscriptionsWithPeriodPrice,
    settingsRepository: SettingsRepository,
    private val calculator: PaymentInfoCalculator,
    private val clock: Clock,
    @Dispatcher(DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) {

    private val paymentPeriodFlow = settingsRepository.getSettingsFlow().map { it.period }

    private val subscriptionsWithCurrencyFilterItemsFlow = combine(
        getSubscription(),
        paymentPeriodFlow,
    ) { subscriptions, period ->
        val totalPrices = calculator.getTotalPricesForPeriod(
            subscriptions.map { it.subscription.paymentInfo },
            period,
        )
        val currencyFilterItems = totalPrices
            .map { SubscriptionFilterItem.Currency(it) }
            .sortedByDescending { it.price.value }
        subscriptions to currencyFilterItems
    }

    operator fun invoke(
        selectedFilterItemsFlow: Flow<List<SubscriptionFilterItem>>,
    ): Flow<SubscriptionsWithFilter> =
        combine(
            subscriptionsWithCurrencyFilterItemsFlow,
            paymentPeriodFlow,
            selectedFilterItemsFlow,
        ) { (subscriptions, currencyFilterItems), period, selectedFilterItems ->
            val filtered = subscriptions.applySelectedFilter(period, selectedFilterItems)
            val filterItems = buildFilter(
                period,
                currencyFilterItems,
                subscriptions,
                selectedFilterItems,
            )
            SubscriptionsWithFilter(filtered, filterItems)
        }.flowOn(defaultDispatcher)

    private fun buildFilter(
        period: PaymentPeriod,
        currencyFilterItems: List<SubscriptionFilterItem.Currency>,
        subscriptions: List<SubscriptionWithPeriodInfo>,
        selectedFilterItems: List<SubscriptionFilterItem>,
    ) = SubscriptionFilter(
        buildList {
            add(SubscriptionFilterItem.CurrentPeriod(period))
            addAll(currencyFilterItems)
            addAll(
                subscriptions
                    .flatMap { it.subscription.categories }
                    .distinct()
                    .map { SubscriptionFilterItem.Category(it) }
                    .sortedBy { it.category.name },
            )
        }.sortedBy { !selectedFilterItems.contains(it) },
        selectedFilterItems,
    )

    private fun List<SubscriptionWithPeriodInfo>.applySelectedFilter(
        period: PaymentPeriod,
        selectedFilterItems: List<SubscriptionFilterItem>,
    ): List<SubscriptionWithPeriodInfo> {
        if (selectedFilterItems.isEmpty()) return this
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        val from = today.getFirstDayOfCurrentPeriod(period)
        val to = today.getLastDayOfCurrentPeriod(period)
        return filter { subscriptionWithPeriodInfo ->
            val appliedFilter = selectedFilterItems.groupBy { it::class }.map { filterGroup ->
                filterGroup.value.map { filterItem ->
                    when (filterItem) {
                        is SubscriptionFilterItem.Currency -> {
                            val subscription = subscriptionWithPeriodInfo.subscription
                            subscription.paymentInfo.price.currency == filterItem.price.currency
                        }

                        is SubscriptionFilterItem.CurrentPeriod -> {
                            subscriptionWithPeriodInfo.nextPaymentDate in from..to
                        }

                        is SubscriptionFilterItem.Category -> {
                            subscriptionWithPeriodInfo
                                .subscription
                                .categories
                                .contains(filterItem.category)
                        }
                    }
                }
            }
            appliedFilter.all { it.any { shouldBeShown -> shouldBeShown } }
        }
    }
}
