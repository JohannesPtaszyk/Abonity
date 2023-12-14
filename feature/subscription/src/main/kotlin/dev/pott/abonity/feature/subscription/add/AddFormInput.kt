package dev.pott.abonity.feature.subscription.add

import dev.pott.abonity.core.entity.PaymentPeriod
import kotlinx.datetime.LocalDate
import java.util.Currency
import java.util.Locale

data class AddFormInput(
    val firstPaymentDate: LocalDate? = null,
    val name: String = "",
    val description: String = "",
    val priceValue: String = "",
    val currency: Currency = Currency.getInstance(Locale.getDefault()),
    val isOneTimePayment: Boolean = false,
    val paymentPeriod: PaymentPeriod? = PaymentPeriod.MONTHS,
    val paymentPeriodCount: Int? = 1,
)
