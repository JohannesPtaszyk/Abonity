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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class AddScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    clock: Clock,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val args = AddScreenDestination.Args.parse(savedStateHandle)

    private val inputState = MutableStateFlow(
        if (args.subscriptionId != null) {
            AddFormState()
        } else {
            AddFormState(paymentDateEpochMillis = clock.now().toEpochMilliseconds())
        },
    )

    private val savingState = MutableStateFlow(AddState.SavingState.IDLE)

    private val loadingState = MutableStateFlow(args.subscriptionId != null)

    val state: StateFlow<AddState> = if (args.subscriptionId != null) {
        val prefilledInputState = flow {
            val subscription = repository.getSubscriptionFlow(args.subscriptionId).first()
            val periodicType = (subscription.paymentInfo.type as? PaymentType.Periodic)
            val priceValue = subscription.paymentInfo.price.value.toString()
            inputState.value = AddFormState(
                TimeUnit.DAYS.toMillis(
                    subscription.paymentInfo.firstPayment.toEpochDays().toLong(),
                ),
                name = subscription.name,
                description = subscription.description,
                priceValue = priceValue,
                currency = subscription.paymentInfo.price.currency,
                isOneTimePayment = subscription.paymentInfo.type == PaymentType.OneTime,
                paymentPeriod = periodicType?.period,
                paymentPeriodCount = periodicType?.periodCount,
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
            savingState,
        ) { input, saving ->
            AddState(input, saving, false)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddState(loading = loadingState.value),
    )

    fun setPaymentDate(epochMilliseconds: Long) {
        updateInputs { it.copy(paymentDateEpochMillis = epochMilliseconds) }
    }

    fun setName(name: String) {
        updateInputs { it.copy(name = name) }
    }

    fun setDescription(description: String) {
        updateInputs { it.copy(description = description) }
    }

    fun setPrice(value: String) {
        updateInputs { it.copy(priceValue = value) }
    }

    fun setCurrency(currency: Currency) {
        updateInputs { it.copy(currency = currency) }
    }

    fun setPeriodic(isPeriodic: Boolean) {
        updateInputs { it.copy(isOneTimePayment = !isPeriodic) }
    }

    fun setPaymentPeriod(paymentPeriod: PaymentPeriod) {
        updateInputs { it.copy(paymentPeriod = paymentPeriod) }
    }

    fun setPaymentPeriodCount(periodCount: String) {
        updateInputs { it.copy(paymentPeriodCount = periodCount.toIntOrNull()) }
    }

    fun save() {
        viewModelScope.launch {
            savingState.value = AddState.SavingState.SAVING
            val input = inputState.value
            val priceValue = parsePriceValue(input.priceValue)
            val price = Price(priceValue, input.currency)
            val date = Instant.fromEpochMilliseconds(input.paymentDateEpochMillis!!)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            val subscription = Subscription(
                name = input.name,
                description = input.description,
                paymentInfo = PaymentInfo(
                    price = price,
                    firstPayment = date,
                    type = if (input.isOneTimePayment) {
                        PaymentType.OneTime
                    } else {
                        PaymentType.Periodic(
                            input.paymentPeriodCount!!,
                            input.paymentPeriod!!,
                        )
                    },
                ),
            )
            repository.addOrUpdateSubscription(subscription)
            savingState.value = AddState.SavingState.SAVED
        }
    }

    private fun parsePriceValue(value: String): Double {
        return value.replace(",", ".").toDouble()
    }

    private fun updateInputs(block: (AddFormState) -> AddFormState) {
        inputState.value = block(inputState.value)
    }
}
