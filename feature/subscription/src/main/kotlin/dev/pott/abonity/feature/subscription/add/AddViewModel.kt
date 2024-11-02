package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.subscription.CategoryRepository
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class AddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clock: Clock,
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<AddDestination>()
    private val argSubscriptionId = SubscriptionId.parse(args.subscriptionId)

    private val formState = MutableStateFlow(
        if (argSubscriptionId != null) {
            AddFormState()
        } else {
            AddFormState(paymentDateEpochMillis = clock.now().toEpochMilliseconds())
        },
    )

    private fun getPrefilledFormState(id: SubscriptionId) =
        flow {
            val subscription = subscriptionRepository.getSubscriptionFlow(id).first()
            if (subscription != null) {
                val periodicType = (subscription.paymentInfo.type as? PaymentType.Periodic)
                val priceValue = subscription.paymentInfo.price.value.toString()
                formState.value = AddFormState(
                    TimeUnit.DAYS.toMillis(
                        subscription.paymentInfo.firstPayment.toEpochDays().toLong(),
                    ),
                    name = ValidatedInput(subscription.name),
                    description = subscription.description,
                    priceValue = ValidatedInput(priceValue),
                    currency = subscription.paymentInfo.price.currency,
                    isOneTimePayment = subscription.paymentInfo.type == PaymentType.OneTime,
                    paymentPeriod = periodicType?.period ?: PaymentPeriod.MONTHS,
                    paymentPeriodCount = ValidatedInput(
                        periodicType?.periodCount?.toString() ?: "1",
                    ),
                    selectedCategories = subscription.categories.toImmutableList(),
                )
            }
            emitAll(formState)
        }

    val state: StateFlow<AddState> = combine(
        if (argSubscriptionId != null) {
            getPrefilledFormState(argSubscriptionId)
        } else {
            formState
        },
        categoryRepository.getCategoriesFlow(),
    ) { formState, categories ->
        AddState(
            showNameAsTitle = args.subscriptionId != null,
            formState = formState,
            categories = categories
                .sortedBy { !formState.selectedCategories.contains(it) }
                .toImmutableList(),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AddState(),
    )

    fun setPaymentDate(epochMilliseconds: Long) {
        formState.update { it.copy(paymentDateEpochMillis = epochMilliseconds) }
    }

    fun setName(name: String) {
        formState.update { it.copy(name = validateName(name)) }
    }

    fun setDescription(description: String) {
        formState.update { it.copy(description = description) }
    }

    fun setPrice(value: String) {
        formState.update { it.copy(priceValue = validatePrice(value)) }
    }

    fun setCurrency(currency: Currency) {
        formState.update { it.copy(currency = currency) }
    }

    fun setPeriodic(isPeriodic: Boolean) {
        formState.update { it.copy(isOneTimePayment = !isPeriodic) }
    }

    fun setPaymentPeriod(paymentPeriod: PaymentPeriod) {
        formState.update { it.copy(paymentPeriod = paymentPeriod) }
    }

    fun setPaymentPeriodCount(periodCount: String) {
        formState.update { it.copy(paymentPeriodCount = validatePaymentPeriod(periodCount)) }
    }

    fun openAddCategoryDialog() {
        formState.update { it.copy(showCategoryDialog = true) }
    }

    fun closeAddCategoryDialog() {
        formState.update { it.copy(showCategoryDialog = false) }
    }

    fun selectCategory(category: Category) {
        formState.update {
            val selectedCategories = if (it.selectedCategories.contains(category)) {
                it.selectedCategories - category
            } else {
                it.selectedCategories + category
            }
            it.copy(selectedCategories = selectedCategories.toImmutableList())
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            val category = categoryRepository.addOrUpdateCategory(Category(name = name))
            formState.update {
                it.copy(selectedCategories = (it.selectedCategories + category).toImmutableList())
            }
            closeAddCategoryDialog()
        }
    }

    fun resetSavingState() {
        formState.update { it.copy(saving = AddState.SavingState.IDLE) }
    }

    private fun validateName(name: String): ValidatedInput {
        val errors = buildList {
            if (name.isBlank()) {
                add(ValidationError.EmptyOrBlank)
            }
        }.toImmutableList()
        return ValidatedInput(name, errors)
    }

    private fun validatePrice(value: String): ValidatedInput {
        val errors = buildList {
            if (value.isBlank()) {
                add(ValidationError.EmptyOrBlank)
            }
        }.toImmutableList()
        return ValidatedInput(value, errors)
    }

    private fun validatePaymentPeriod(periodCount: String): ValidatedInput {
        val errors = buildList {
            if (periodCount.isEmpty()) {
                add(ValidationError.EmptyOrBlank)
            } else if (periodCount.toInt() <= 0) {
                add(ValidationError.MustBePositiveValue)
            }
        }.toImmutableList()
        return ValidatedInput(periodCount, errors)
    }

    fun save() {
        viewModelScope.launch {
            formState.update { it.copy(saving = AddState.SavingState.SAVING) }
            val validatedState = formState.updateAndGet { formState ->
                formState.copy(
                    name = validateName(formState.name.value),
                    priceValue = validatePrice(formState.priceValue.value),
                )
            }
            if (!validatedState.isValid) {
                formState.update { it.copy(saving = AddState.SavingState.ERROR) }
                return@launch
            }
            val subscription = getSubscription()
            subscriptionRepository.addOrUpdateSubscription(subscription)
            formState.update { it.copy(saving = AddState.SavingState.SAVED) }
        }
    }

    private fun getSubscription(): Subscription {
        val input = formState.value
        val priceValue = parsePriceValue(input.priceValue.value)
        val price = Price(priceValue, input.currency)
        val date = calculateDate(input)
        val paymentType = if (input.isOneTimePayment) {
            PaymentType.OneTime
        } else {
            PaymentType.Periodic(
                input.paymentPeriodCount.value.toInt(),
                input.paymentPeriod,
            )
        }
        val paymentInfo = PaymentInfo(
            price = price,
            firstPayment = date,
            type = paymentType,
        )
        return Subscription(
            id = argSubscriptionId ?: SubscriptionId.none(),
            name = input.name.value,
            description = input.description,
            paymentInfo = paymentInfo,
            categories = input.selectedCategories,
        )
    }

    private fun calculateDate(input: AddFormState): LocalDate {
        val instant = if (input.paymentDateEpochMillis != null) {
            Instant.fromEpochMilliseconds(input.paymentDateEpochMillis)
        } else {
            clock.now()
        }
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    private fun parsePriceValue(value: String): Double = value.replace(",", ".").toDouble()
}
