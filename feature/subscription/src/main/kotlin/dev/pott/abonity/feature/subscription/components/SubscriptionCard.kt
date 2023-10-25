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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.pott.abonity.core.ui.preview.MultiPreview
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.feature.subscription.overview.SelectableSubscriptionWithPeriodPrice
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
            PaymentInfo(subscription.paymentInfo, periodPrice)
        }
        Spacer(modifier = modifier.height(16.dp))
    }
}

@Composable
private fun PaymentInfo(
    paymentInfo: PaymentInfo,
    price: Price,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.End) {
        FormattedPrice(
            price = price,
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier,
            maxLines = 1
        )

        val paymentType = paymentInfo.type
        if (paymentType is PaymentType.Periodic && paymentInfo.price != price) {
            PeriodicPriceInfo(paymentType, paymentInfo)
        }
    }
}

@MultiPreview
@Composable
fun SubscriptionCardPreview(
    @PreviewParameter(SubscriptionCardPreviewProvider::class) item: SelectableSubscriptionWithPeriodPrice
) {
    AppTheme {
        SubscriptionCard(
            subscription = item.subscription,
            periodPrice = item.periodPrice,
            onClick = {},
            isSelected = false
        )
    }
}

private class SubscriptionCardPreviewProvider :
    PreviewParameterProvider<SelectableSubscriptionWithPeriodPrice> {
    override val values: Sequence<SelectableSubscriptionWithPeriodPrice>
        get() {
            val currency = Currency.getInstance("EUR")
            return sequenceOf(
                SelectableSubscriptionWithPeriodPrice(
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
                    Price(999.11, currency),
                    isSelected = false,
                ),
                SelectableSubscriptionWithPeriodPrice(
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
                    Price(0.99, Currency.getInstance("USD")),
                    isSelected = true
                )
            )
        }
}
