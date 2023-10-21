package dev.pott.abonity.feature.subscription.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.viewmodel.LazyInitViewModel
import dev.pott.abonity.feature.subscription.overview.SubscriptionItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubscriptionRepository,
    private val calculator: PeriodicPriceCalculator,
) :
    LazyInitViewModel<DetailState>(DetailState()) {

    private val args = DetailScreenDestination.getArgs(savedStateHandle)
    private val detailIdFlow = MutableStateFlow(
        args.id?.let { SubscriptionId(it) }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize() {
        viewModelScope.launch {
            detailIdFlow.flatMapLatest { id ->
                if (id != null) {
                    repository.getSubscription(id)
                } else {
                    flowOf(null)
                }
            }.collect { subscription ->
                val item = subscription?.let {
                    SubscriptionItem(
                        subscription,
                        calculator.calculateForPeriod(
                            paymentInfo = subscription.paymentInfo,
                            period = PeriodicPriceCalculator.Period.MONTH
                        )
                    )
                }
                updateState { copy(subscription = item) }

            }
        }
    }

    fun setId(detailId: SubscriptionId?) {
        detailIdFlow.value = detailId
    }
}
