package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.Currency

class AddScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val clock: Clock,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val args = AddScreenDestination.getArgs(savedStateHandle)
    private val isEditMode = args.subscriptionId != null

    val state = MutableStateFlow(AddState(null, false))

    fun updateInputs(input: AddFormInput) {
        Logger.i { input.toString() + isEditMode }
        viewModelScope.launch {
            val subcription = Subscription(
                name = input.name,
                description = input.description,
                paymentInfo = PaymentInfo(
                    price = Price(12.0, Currency.getInstance("EUR")),
                    firstPayment = clock.todayIn(TimeZone.currentSystemDefault()),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
            )
            repository.addOrUpdateSubscription(subcription)
        }
    }
}
