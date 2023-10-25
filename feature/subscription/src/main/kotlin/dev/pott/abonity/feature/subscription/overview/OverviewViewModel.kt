package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator,
) : ViewModel() {

    private val selectedDetailId = MutableStateFlow<SubscriptionId?>(null)

    val state = combine(
        selectedDetailId,
        repository.getSubscriptionsFlow()
    ) { detailId, subscriptions ->
        OverviewState(
            periodSubscriptions = subscriptions.mapToSubscriptionItems(detailId),
            detailId = detailId,
        )
    }.onEach {
        Logger.withTag(this::class.java.simpleName).v { it.toString() }
    }.stateIn(
        scope = viewModelScope,
        initialValue = OverviewState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    private fun List<Subscription>.mapToSubscriptionItems(
        selectedId: SubscriptionId?,
    ): List<SelectableSubscriptionWithPeriodPrice> {
        return map { subscription ->
            SelectableSubscriptionWithPeriodPrice(
                subscription,
                calculator.calculateForPeriod(
                    paymentInfo = subscription.paymentInfo,
                    period = PeriodicPriceCalculator.Period.MONTH
                ),
                isSelected = selectedId == subscription.id
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
