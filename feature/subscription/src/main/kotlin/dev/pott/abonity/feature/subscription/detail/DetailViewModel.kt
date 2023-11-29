package dev.pott.abonity.feature.subscription.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.PaymentDateCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val calculator: PaymentDateCalculator,
) : ViewModel() {
    private val currentDetailId: MutableStateFlow<SubscriptionId?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = currentDetailId.flatMapLatest { id ->
        if (id != null) {
            repository.getSubscriptionFlow(id)
        } else {
            flowOf(null)
        }
    }.map { subscription ->
        val type = subscription?.paymentInfo?.type
        val nextPayment = if (type is PaymentType.Periodic) {
            calculator.calculateNextPossiblePaymentDate(type)
        } else {
            null
        }
        DetailState(
            subscription = subscription,
            nextPayment = nextPayment,
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = DetailState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )

    fun setId(detailId: SubscriptionId?) {
        currentDetailId.value = detailId
    }
}
