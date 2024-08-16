package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
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
    showCategories: Boolean = true,
    date: @Composable () -> Unit = {},
) {
    val colors = if (isSelected) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        border = if (isSelected) {
            BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            null
        },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            val (name, description, categories, paymentInfo, dateRef) = createRefs()
            val barrier = createStartBarrier(paymentInfo, dateRef)
            Text(
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top)
                    linkTo(parent.start, barrier, bias = 0f, endMargin = 8.dp)
                    width = Dimension.fillToConstraints
                },
                text = subscription.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.constrainAs(description) {
                    top.linkTo(name.bottom, margin = 8.dp)
                    linkTo(parent.start, barrier, bias = 0f, endMargin = 8.dp)
                    visibility = if (subscription.description != null) {
                        Visibility.Visible
                    } else {
                        Visibility.Gone
                    }
                    width = Dimension.fillToConstraints
                },
                text = subscription.description.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Categories(
                modifier = Modifier.constrainAs(categories) {
                    top.linkTo(description.bottom, margin = 16.dp)
                    linkTo(parent.start, barrier)
                    width = Dimension.fillToConstraints
                    visibility = if (showCategories) {
                        Visibility.Visible
                    } else {
                        Visibility.Gone
                    }
                },
                categories = subscription.categories.toImmutableList(),
            )
            PaymentInfo(
                modifier = Modifier.constrainAs(paymentInfo) {
                    start.linkTo(barrier)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                paymentInfo = subscription.paymentInfo,
                price = periodPrice,
                currentPeriod = currentPeriod,
            )
            Box(
                modifier = Modifier.constrainAs(dateRef) {
                    linkTo(paymentInfo.bottom, parent.bottom, bias = 1f)
                    end.linkTo(parent.end)
                },
            ) {
                date()
            }
        }
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
            color = MaterialTheme.colorScheme.primary,
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
        ) {
            Text(
                text = "Date",
                style = MaterialTheme.typography.bodySmall,
            )
        }
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
