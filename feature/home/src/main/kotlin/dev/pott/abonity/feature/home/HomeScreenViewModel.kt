package dev.pott.abonity.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.usecase.GetSubscriptionsWithPeriodPriceUntilEndOfPeriod
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    getSubscriptionsWithPeriodPriceUntilEndOfPeriod:
    GetSubscriptionsWithPeriodPriceUntilEndOfPeriod,
) : ViewModel() {

    private val selectedId: MutableStateFlow<SubscriptionId?> = MutableStateFlow(null)

    val state = combine(
        getSubscriptionsWithPeriodPriceUntilEndOfPeriod(PaymentPeriod.MONTHS),
        selectedId,
    ) { subscriptions, selectedId ->
        HomeState(
            upcommingSubscriptions = subscriptions.toImmutableList(),
            selectedId = selectedId,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeState(),
    )
}
