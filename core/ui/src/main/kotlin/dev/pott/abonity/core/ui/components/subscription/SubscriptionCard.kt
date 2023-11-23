package dev.pott.abonity.core.ui.components.subscription

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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.PreviewCommonUiConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.datetime.LocalDate
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionCard(
    subscription: Subscription,
    periodPrice: Price,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier, onClick = onClick, enabled = !isSelected) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            PaymentInfo(subscription.paymentInfo, periodPrice)
        }
        Spacer(Modifier.height(16.dp))
        FirstPayment(subscription.paymentInfo.firstPayment)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FirstPayment(date: LocalDate) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.secondary,
        LocalTextStyle provides MaterialTheme.typography.labelSmall,
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = stringResource(id = R.string.subscription_card_first_payment_label))
            FormattedDate(date = date)
        }
    }
}

@Composable
private fun PaymentInfo(paymentInfo: PaymentInfo, price: Price, modifier: Modifier = Modifier) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.primary,
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = modifier) {
            FormattedPrice(
                price = price,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
            )

            val paymentType = paymentInfo.type
            if (paymentType is PaymentType.Periodic && paymentInfo.price != price) {
                PeriodicPriceInfo(paymentType, paymentInfo)
            }
        }
    }
}

@PreviewCommonUiConfig
@Composable
private fun SubscriptionCardPreview(
    @PreviewParameter(SubscriptionCardPreviewProvider::class)
    item: SubscriptionWithPeriodInfo,
) {
    AppTheme {
        SubscriptionCard(
            subscription = item.subscription,
            periodPrice = item.periodPrice,
            onClick = {},
            isSelected = false,
        )
    }
}

private class SubscriptionCardPreviewProvider :
    PreviewParameterProvider<SubscriptionWithPeriodInfo> {
    override val values: Sequence<SubscriptionWithPeriodInfo>
        get() {
            val currency = Currency.getInstance("EUR")
            return sequenceOf(
                SubscriptionWithPeriodInfo(
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
                                PaymentPeriod.MONTHS,
                            ),
                        ),
                    ),
                    Price(999.11, currency),
                    LocalDate(2023, 12, 12),
                ),
                SubscriptionWithPeriodInfo(
                    Subscription(
                        SubscriptionId(1),
                        "One Time Payment",
                        "This is a one time payment",
                        PaymentInfo(
                            Price(
                                0.99,
                                Currency.getInstance("USD"),
                            ),
                            LocalDate(1999, 1, 1),
                            PaymentType.OneTime,
                        ),
                    ),
                    Price(0.99, Currency.getInstance("USD")),
                    LocalDate(2023, 12, 12),
                ),
            )
        }
}
