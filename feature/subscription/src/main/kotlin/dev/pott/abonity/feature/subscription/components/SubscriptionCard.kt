package dev.pott.abonity.feature.subscription.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.MultiPreview
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.core.ui.util.getDefaultLocale
import dev.pott.abonity.feature.subscription.overview.SubscriptionItem
import kotlinx.datetime.LocalDate
import java.text.NumberFormat
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionCard(
    item: SubscriptionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val subscription = item.subscription
    Card(modifier = modifier, onClick = onClick) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subscription.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            PaymentInfo(item.subscription.paymentInfo, item.periodPrice)
        }
        Spacer(modifier = modifier.height(16.dp))
    }
}

@Composable
private fun PaymentInfo(
    paymentInfo: PaymentInfo,
    periodPrice: Price,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.End) {
        val formattedPeriodPrice by rememberFormattedPrice(
            periodPrice.value,
            periodPrice.currency
        )
        Text(
            text = formattedPeriodPrice,
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier,
            maxLines = 1
        )

        val paymentType = paymentInfo.type
        if (paymentType is PaymentType.Periodic && paymentInfo.price != periodPrice) {
            PeriodicPriceInfo(paymentType, paymentInfo)
        }
    }
}

@Composable
private fun PeriodicPriceInfo(
    paymentType: PaymentType.Periodic,
    paymentInfo: PaymentInfo,
    modifier: Modifier = Modifier,
) {
    val formattedPeriodPrice by rememberFormattedPrice(
        paymentInfo.price.value,
        paymentInfo.price.currency
    )
    val period = when (paymentType.period) {
        PaymentPeriod.DAYS -> pluralStringResource(
            id = R.plurals.payment_period_days,
            count = paymentType.periodCount,
            formattedPeriodPrice,
            paymentType.periodCount,
        )

        PaymentPeriod.WEEKS -> pluralStringResource(
            id = R.plurals.payment_period_weeks,
            count = paymentType.periodCount,
            formattedPeriodPrice,
            paymentType.periodCount,
        )

        PaymentPeriod.MONTHS -> pluralStringResource(
            id = R.plurals.payment_period_months,
            count = paymentType.periodCount,
            formattedPeriodPrice,
            paymentType.periodCount,
        )

        PaymentPeriod.YEARS -> pluralStringResource(
            id = R.plurals.payment_period_weeks,
            count = paymentType.periodCount,
            formattedPeriodPrice,
            paymentType.periodCount,
        )
    }
    Text(
        style = MaterialTheme.typography.labelSmall.copy(
            lineBreak = LineBreak(
                LineBreak.Strategy.HighQuality,
                LineBreak.Strictness.Strict,
                wordBreak = LineBreak.WordBreak.Phrase
            )
        ),
        text = "$period",
        modifier = modifier,
        textAlign = TextAlign.End,
    )
}

@Composable
private fun rememberFormattedPrice(
    value: Double,
    currency: Currency,
): State<String> {
    val defaultLocale = getDefaultLocale()
    return remember {
        derivedStateOf {
            val format = NumberFormat.getCurrencyInstance(defaultLocale)
            format.currency = currency
            format.format(value)
        }
    }
}

@MultiPreview
@Composable
fun SubscriptionCardPreview(
    @PreviewParameter(PreviewProvider::class) item: SubscriptionItem
) {
    AppTheme {
        SubscriptionCard(item = item) { }
    }
}

private class PreviewProvider :
    PreviewParameterProvider<SubscriptionItem> {
    override val values: Sequence<SubscriptionItem>
        get() {
            val currency = Currency.getInstance("EUR")
            return sequenceOf(
                SubscriptionItem(
                    Subscription(
                        SubscriptionId(0),
                        "Periodic Subscription",
                        """
                            This is a periodic subscription 
                            with a really long description, 
                            because we need to make sure to 
                            fit every kind of text into this 
                            ui!
                        """.trim(),
                        PaymentInfo(
                            Price(9999.11, currency),
                            LocalDate(2022, 12, 12),
                            PaymentType.Periodic(
                                1,
                                PaymentPeriod.MONTHS
                            )
                        ),
                    ),
                    Price(999.11, currency)
                ),
                SubscriptionItem(
                    Subscription(
                        SubscriptionId(1),
                        "One Time Payment",
                        "This is a one time payment",
                        PaymentInfo(
                            Price(
                                0.99,
                                Currency.getInstance("USD")
                            ),
                            LocalDate(1999, 1, 1),
                            PaymentType.OneTime
                        ),
                    ),
                    Price(0.99, Currency.getInstance("USD"))
                )
            )
        }
}
