package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PaymentInfoCalculator
import dev.pott.abonity.core.domain.usecase.GetSubscriptionsWithPeriodPrice
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSubscriptionWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
    private val calculator: PaymentInfoCalculator,
) : ViewModel() {
    private val args = OverviewScreenDestination.Args.parse(savedStateHandle)
    private val selectedDetailId = MutableStateFlow(args.detailId)

    val state = combine(
        selectedDetailId,
        getSubscriptionWithPeriodPrice(),
    ) { detailId, subscriptions ->
        val periodPrices = calculator.getTotalPricesForPeriod(
            subscriptions.map { it.subscription.paymentInfo },
            PaymentPeriod.MONTHS,
        )

        OverviewState(
            periodSubscriptions = subscriptions.toImmutableList(),
            detailId = detailId,
            periodPrices = periodPrices.toImmutableList(),
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = OverviewState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )

    fun openDetails(id: SubscriptionId) {
        selectedDetailId.value = id
    }

    fun consumeDetails() {
        selectedDetailId.value = null
    }
}
