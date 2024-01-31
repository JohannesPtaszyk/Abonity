package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.ui.R

@Composable
fun PeriodicPriceInfo(
    paymentType: PaymentType.Periodic,
    paymentInfo: PaymentInfo,
    modifier: Modifier = Modifier,
) {
    val formattedPeriodPrice = rememberFormattedPrice(
        paymentInfo.price.value,
        paymentInfo.price.currency,
    )
    val period = when (paymentType.period) {
        PaymentPeriod.DAYS ->
            pluralStringResource(
                id = R.plurals.payment_period_days,
                count = paymentType.periodCount,
                formattedPeriodPrice,
                paymentType.periodCount,
            )

        PaymentPeriod.WEEKS ->
            pluralStringResource(
                id = R.plurals.payment_period_weeks,
                count = paymentType.periodCount,
                formattedPeriodPrice,
                paymentType.periodCount,
            )

        PaymentPeriod.MONTHS ->
            pluralStringResource(
                id = R.plurals.payment_period_months,
                count = paymentType.periodCount,
                formattedPeriodPrice,
                paymentType.periodCount,
            )

        PaymentPeriod.YEARS ->
            pluralStringResource(
                id = R.plurals.payment_period_years,
                count = paymentType.periodCount,
                formattedPeriodPrice,
                paymentType.periodCount,
            )
    }
    Text(
        style = MaterialTheme.typography.labelSmall,
        text = period,
        modifier = modifier,
        textAlign = TextAlign.End,
    )
}
