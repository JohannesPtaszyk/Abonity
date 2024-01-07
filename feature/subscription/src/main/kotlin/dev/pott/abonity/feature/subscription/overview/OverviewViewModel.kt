package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.common.injection.Dispatcher
import dev.pott.abonity.common.injection.Dispatcher.Type.DEFAULT
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.getFirstDayOfCurrentPeriod
import dev.pott.abonity.core.domain.subscription.getLastDayOfCurrentPeriod
import dev.pott.abonity.core.domain.subscription.usecase.GetSubscriptionsWithPeriodPrice
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilterState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSubscriptionWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
    @Dispatcher(DEFAULT) defaultDispatcher: CoroutineDispatcher,
    private val clock: Clock,
    private val calculator: PaymentInfoCalculator,
) : ViewModel() {

    private val args = OverviewScreenDestination.Args.parse(savedStateHandle)

    private val selectedDetailIdFlow = MutableStateFlow(args.detailId)

    private val selectedFilterItemsFlow = MutableStateFlow<ImmutableList<SubscriptionFilterItem>>(
        persistentListOf(),
    )
    private val subscriptionInformationFlow = combine(
        getSubscriptionWithPeriodPrice(),
        selectedFilterItemsFlow,
    ) { subscriptions, selectedFilterItems ->
        val totalPricePerCurrency = calculator.getTotalPricesForPeriod(
            subscriptions.map { it.subscription.paymentInfo },
            PaymentPeriod.MONTHS,
        )
        val filteredSubscriptions = if (selectedFilterItems.isEmpty()) {
            subscriptions
        } else {
            subscriptions.filter { subscriptionWithPeriodInfo ->
                val appliedFilter = selectedFilterItems.map { filterItem ->
                    when (filterItem) {
                        is SubscriptionFilterItem.Currency -> {
                            val subscription = subscriptionWithPeriodInfo.subscription
                            subscription.paymentInfo.price.currency == filterItem.price.currency
                        }

                        is SubscriptionFilterItem.CurrentPeriod -> {
                            val today = clock.todayIn(TimeZone.currentSystemDefault())
                            val from = today.getFirstDayOfCurrentPeriod(PaymentPeriod.MONTHS)
                            val to = today.getLastDayOfCurrentPeriod(PaymentPeriod.MONTHS)
                            subscriptionWithPeriodInfo.nextPaymentDate in from..to
                        }
                    }
                }
                appliedFilter.contains(true)
            }
        }
        SubscriptionInformation(
            filteredSubscriptions,
            totalPricePerCurrency,
        )
    }.flowOn(defaultDispatcher)

    val state = combine(
        selectedDetailIdFlow,
        selectedFilterItemsFlow,
        subscriptionInformationFlow,
    ) { detailId, selectedFilterItems, subscriptionsAndTotalPrices ->
        val (subscriptions, totalPrices) = subscriptionsAndTotalPrices
        val filterState = SubscriptionFilterState(
            totalPrices,
            PaymentPeriod.MONTHS,
            selectedFilterItems,
        )

        OverviewState.Loaded(
            subscriptions = subscriptions.toImmutableList(),
            detailId = detailId,
            filterState = filterState,
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = OverviewState.Loading,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )

    fun openDetails(id: SubscriptionId) {
        selectedDetailIdFlow.value = id
    }

    fun consumeDetails() {
        selectedDetailIdFlow.value = null
    }

    fun toggleFilter(item: SubscriptionFilterItem) {
        viewModelScope.launch {
            selectedFilterItemsFlow.update {
                if (it.contains(item)) {
                    it - item
                } else {
                    it + item
                }.toImmutableList()
            }
        }
    }

    private data class SubscriptionInformation(
        val filteredSubscriptions: List<SubscriptionWithPeriodInfo>,
        val currencyFilterItems: List<Price>,
    )
}
