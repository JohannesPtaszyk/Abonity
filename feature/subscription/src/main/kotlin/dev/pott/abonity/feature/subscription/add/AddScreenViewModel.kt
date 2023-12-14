package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class AddScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clock: Clock,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val args = AddScreenDestination.Args.parse(savedStateHandle)

    private val inputState = MutableStateFlow(
        if(args.subscriptionId != null) {
            AddFormInput()
        } else {
            AddFormInput(firstPaymentDate = clock.todayIn(TimeZone.currentSystemDefault()))
        }
    )

    private val savingState = MutableStateFlow(AddState.SavingState.IDLE)

    private val loadingState = MutableStateFlow(args.subscriptionId != null)

    val state: StateFlow<AddState> = if (args.subscriptionId != null) {
        val prefilledInputState = flow {
            val subscription = repository.getSubscriptionFlow(args.subscriptionId).first()
            inputState.value = AddFormInput(
                subscription.paymentInfo.firstPayment,
                name = subscription.name,
                description = subscription.description,
                priceValue = subscription.paymentInfo.price.value.toString(),
                currency = subscription.paymentInfo.price.currency,
                isOneTimePayment = subscription.paymentInfo.type == PaymentType.OneTime,
                paymentPeriod = (subscription.paymentInfo.type as? PaymentType.Periodic)?.period,
                paymentPeriodCount = (subscription.paymentInfo.type as? PaymentType.Periodic)?.periodCount,
            )
            emitAll(inputState)
        }
        combine(
            prefilledInputState,
            savingState,
            loadingState,
        ) { prefilledInputState, saving, loading ->
            AddState(prefilledInputState, saving, loading)
        }
    } else {
        combine(
            inputState,
            savingState
        ) { input, saving ->
            AddState(input, saving, false)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddState(loading = loadingState.value)
    )

    @Suppress("MagicNumber")
    fun updateInputs(input: AddFormInput) {
        inputState.value = input
    }

    fun save() {
        viewModelScope.launch {
            savingState.value = AddState.SavingState.SAVING
            val input = inputState.value
            val subscription = Subscription(
                name = input.name,
                description = input.description,
                paymentInfo = PaymentInfo(
                    price = Price(input.priceValue.toDouble(), input.currency),
                    firstPayment = clock.todayIn(TimeZone.currentSystemDefault()),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
            )
            repository.addOrUpdateSubscription(subscription)
            savingState.value = AddState.SavingState.SAVED
        }
    }
}
