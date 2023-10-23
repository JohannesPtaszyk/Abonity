package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.viewmodel.LazyInitViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator,
) : LazyInitViewModel<OverviewState>(OverviewState()) {

    override fun initialize() {
        viewModelScope.launch {
            repository.getSubscriptionFlow().map { subscriptions ->
                subscriptions.map { subscription ->
                    SubscriptionItem(
                        subscription,
                        calculator.calculateForPeriod(
                            paymentInfo = subscription.paymentInfo,
                            period = PeriodicPriceCalculator.Period.MONTH
                        )
                    )
                }
            }.collect {
                updateState { copy(subscriptions = it) }
            }
        }
    }

    fun openDetails(id: SubscriptionId) {
        viewModelScope.launch {
            updateState { copy(detailId = id) }
        }
    }

    fun consumeDetails() {
        updateState { copy(detailId = null) }
    }
}
