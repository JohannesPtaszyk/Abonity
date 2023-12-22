package dev.pott.abonity.core.ui.string

import androidx.compose.runtime.Composable
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.ui.R

@Composable
fun paymentPeriodPluralRes(it: PaymentPeriod): Int {
    val textRes = when (it) {
        PaymentPeriod.DAYS -> R.plurals.payment_period_dropdown_days
        PaymentPeriod.WEEKS -> R.plurals.payment_period_dropdown_weeks
        PaymentPeriod.MONTHS -> R.plurals.payment_period_dropdown_months
        PaymentPeriod.YEARS -> R.plurals.payment_period_dropdown_years
    }
    return textRes
}
