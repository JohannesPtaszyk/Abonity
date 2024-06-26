package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.components.subscription.categories.Categories
import dev.pott.abonity.core.ui.preview.PreviewCommonUiConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import java.util.Currency

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    periodPrice: Price,
    currentPeriod: PaymentPeriod,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        } else {
            CardDefaults.cardColors()
        },
        border = if (isSelected) {
            BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            null
        },
    ) {
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
                subscription.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Categories(
                    subscription.categories.toImmutableList(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.width(8.dp))
            PaymentInfo(subscription.paymentInfo, periodPrice, currentPeriod)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PaymentInfo(
    paymentInfo: PaymentInfo,
    price: Price,
    currentPeriod: PaymentPeriod,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        FormattedPrice(
            price = price,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
        )

        val paymentType = paymentInfo.type
        if (paymentType is PaymentType.Periodic && paymentType.period != currentPeriod) {
            PeriodicPriceInfo(paymentType, paymentInfo)
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
            currentPeriod = PaymentPeriod.MONTHS,
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
                        listOf(Category(name = "Category")),
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
                        listOf(Category(name = "Category")),
                    ),
                    Price(0.99, Currency.getInstance("USD")),
                    LocalDate(2023, 12, 12),
                ),
            )
        }
}
