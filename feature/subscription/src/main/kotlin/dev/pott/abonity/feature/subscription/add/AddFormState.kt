package dev.pott.abonity.feature.subscription.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.ui.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.Currency
import java.util.Locale

data class AddFormState(
    val paymentDateEpochMillis: Long? = null,
    val name: ValidatedInput = ValidatedInput(""),
    val description: String? = null,
    val priceValue: ValidatedInput = ValidatedInput(""),
    val currency: Currency = Currency.getInstance(Locale.getDefault()),
    val isOneTimePayment: Boolean = false,
    val paymentPeriod: PaymentPeriod = PaymentPeriod.MONTHS,
    val paymentPeriodCount: ValidatedInput = ValidatedInput("1"),
) {
    val isValid = paymentDateEpochMillis != null &&
        !name.isError &&
        !priceValue.isError &&
        !paymentPeriodCount.isError
}

data class ValidatedInput(
    val value: String,
    val errors: ImmutableList<ValidationError> = persistentListOf(),
) {
    val isError = errors.isNotEmpty()
}

sealed interface ValidationError {
    data object EmptyOrBlank : ValidationError
    data object MustBePositiveValue : ValidationError
}

@Composable
fun ValidatedInput.localizedError(): String? {
    if (!isError) return null
    // SimplifiableCallChain disabled because compose scope is required for stringResource
    @Suppress("SimplifiableCallChain")
    return errors.map {
        when (it) {
            ValidationError.EmptyOrBlank -> stringResource(id = R.string.add_validation_error_empty)
            ValidationError.MustBePositiveValue -> stringResource(
                id = R.string.add_validation_error_must_be_positive,
            )
        }
    }.joinToString(System.lineSeparator())
}
