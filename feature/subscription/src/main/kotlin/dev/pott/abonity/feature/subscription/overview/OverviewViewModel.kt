package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator,
) : ViewModel() {

    private val selectedDetailId = MutableStateFlow<SubscriptionId?>(null)

    val state = combine(
        repository.getSubscriptionsFlow(),
        selectedDetailId
    ) { subscriptions, detailId ->
        OverviewState(
            subscriptions = subscriptions.mapToSubscriptionItems(),
            detailId = detailId,
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = OverviewState(),
        started = SharingStarted.WhileSubscribed()
    )

    private fun List<Subscription>.mapToSubscriptionItems(): List<SubscriptionItem> {
        return map { subscription ->
            SubscriptionItem(
                subscription,
                calculator.calculateForPeriod(
                    paymentInfo = subscription.paymentInfo,
                    period = PeriodicPriceCalculator.Period.MONTH
                )
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
