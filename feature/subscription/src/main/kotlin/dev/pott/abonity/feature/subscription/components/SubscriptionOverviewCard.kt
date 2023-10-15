package dev.pott.abonity.feature.subscription.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.HexColor
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.core.ui.util.getDefaultLocale
import dev.pott.abonity.feature.subscription.overview.SubscriptionOverviewItem
import kotlinx.datetime.LocalDate
import java.text.NumberFormat
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionOverviewCard(
    item: SubscriptionOverviewItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val subscription = item.subscription
    Card(modifier = modifier, onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Column {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = subscription.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Price(item.periodPrice)
            }
            val paymentInfo = subscription.paymentInfo
            val paymentType = paymentInfo.type
            if (paymentType is PaymentType.Periodic) {
                PeriodicPriceInfo(paymentType, paymentInfo)
            }
        }
    }
}

@Composable
private fun PeriodicPriceInfo(
    paymentType: PaymentType.Periodic,
    paymentInfo: PaymentInfo
) {
    val period by remember {
        derivedStateOf {
            when (paymentType.period) {
                PaymentPeriod.DAYS -> "days"
                PaymentPeriod.WEEKS -> "weeks"
                PaymentPeriod.MONTHS -> "months"
                PaymentPeriod.YEARS -> "years"
            }
        }
    }
    val formattedPeriodPrice by rememberFormattedPrice(
        paymentInfo.price.value,
        paymentInfo.price.currency
    )
    Text(
        style = MaterialTheme.typography.labelSmall,
        text = "$formattedPeriodPrice every ${paymentType.periodCount} $period"
    )
}

@Composable
private fun Price(
    periodPrice: Price,
    modifier: Modifier = Modifier
) {
    val formattedPeriodPrice by rememberFormattedPrice(
        periodPrice.value,
        periodPrice.currency
    )
    Text(
        text = formattedPeriodPrice,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
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

@Preview
@Composable
fun SubscriptionCardPreview(
    @PreviewParameter(PreviewProvider::class) item: SubscriptionOverviewItem
) {
    AppTheme {
        SubscriptionOverviewCard(item = item) { }
    }
}

private class PreviewProvider :
    PreviewParameterProvider<SubscriptionOverviewItem> {
    override val values: Sequence<SubscriptionOverviewItem>
        get() {
            val currency = Currency.getInstance("EUR")
            return sequenceOf(
                SubscriptionOverviewItem(
                    Subscription(
                        0,
                        "Periodic Subscription",
                        "This is a periodic subscription",
                        PaymentInfo(
                            Price(9999.11, currency),
                            LocalDate(2022, 12, 12),
                            PaymentType.Periodic(
                                1,
                                PaymentPeriod.MONTHS
                            )
                        ),
                        HexColor("#FF5733")
                    ),
                    Price(999.11, currency)
                ),
                SubscriptionOverviewItem(
                    Subscription(
                        0,
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
                        HexColor("#FF6933")
                    ),
                    Price(0.99, Currency.getInstance("USD"))
                )
            )
        }
}
