package dev.pott.abonity.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.usecase.GetUpcomingSubscriptionsUseCase
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUpcomingSubscriptions: GetUpcomingSubscriptionsUseCase,
) : ViewModel() {

    private val selectedId: MutableStateFlow<SubscriptionId?> = MutableStateFlow(null)

    val state = combine(
        getUpcomingSubscriptions(PaymentPeriod.MONTHS),
        selectedId,
    ) { subscriptions, selectedId ->
        DashboardState(
            upcomingSubscriptions = subscriptions.toImmutableList(),
            selectedId = selectedId,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardState(),
    )

    fun select(subscriptionId: SubscriptionId) {
        selectedId.value = subscriptionId
    }

    fun consumeSelectedId() {
        selectedId.value = null
    }
}
