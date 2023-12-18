package dev.pott.abonity.feature.subscription.add

import dev.pott.abonity.core.entity.PaymentPeriod
import java.util.Currency
import java.util.Locale

data class AddFormState(
    val paymentDateEpochMillis: Long? = null,
    val name: String = "",
    val description: String = "",
    val priceValue: String = "",
    val currency: Currency = Currency.getInstance(Locale.getDefault()),
    val isOneTimePayment: Boolean = false,
    val paymentPeriod: PaymentPeriod? = null,
    val paymentPeriodCount: Int? = null,
)
