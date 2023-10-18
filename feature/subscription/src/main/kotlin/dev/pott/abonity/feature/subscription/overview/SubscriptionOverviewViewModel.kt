package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.ui.viewmodel.LazyInitViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionOverviewViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator
) : LazyInitViewModel<SubscriptionOverviewState>(SubscriptionOverviewState()) {

    override fun initialize() {
        viewModelScope.launch {
            repository.getSubscriptionFlow().map {
                it.map { subscription ->
                    SubscriptionOverviewItem(
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
}
