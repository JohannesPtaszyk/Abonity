package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel
@Inject
constructor(
    repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator,
) : ViewModel() {
    private val selectedDetailId = MutableStateFlow<SubscriptionId?>(null)

    val state =
        combine(
            selectedDetailId,
            repository.getSubscriptionsFlow(),
        ) { detailId, subscriptions ->
            OverviewState(
                periodSubscriptions = subscriptions.mapToSubscriptionItems(detailId),
                detailId = detailId,
                periodPrices = calculator.calculateTotalForPeriod(
                    subscriptions.map { it.paymentInfo },
                    PaymentPeriod.MONTHS,
                ).toImmutableList(),
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = OverviewState(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        )

    private fun List<Subscription>.mapToSubscriptionItems(
        selectedId: SubscriptionId?,
    ): List<SelectableSubscriptionWithPeriodPrice> {
        return map { subscription ->
            SelectableSubscriptionWithPeriodPrice(
                subscription,
                calculator.calculateForPeriod(
                    paymentInfo = subscription.paymentInfo,
                    targetPeriod = PaymentPeriod.MONTHS,
                ),
                isSelected = selectedId == subscription.id,
            )
        }
    }

    fun openDetails(id: SubscriptionId) {
        selectedDetailId.value = id
    }

    fun consumeDetails() {
        selectedDetailId.value = null
    }
}
