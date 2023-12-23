package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

@Composable
fun OverviewScreenWithDetails(
    overviewState: OverviewState,
    detailState: DetailState,
    onSubscriptionClicked: (id: SubscriptionId) -> Unit,
    onEditClick: (id: SubscriptionId) -> Unit,
    closeDetails: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Row(modifier = modifier.fillMaxSize()) {
        OverviewScreen(
            state = overviewState,
            onSubscriptionClick = {
                onSubscriptionClicked(it)
            },
            listState = listState,
            modifier = Modifier.weight(1f),
        )

        DetailScreen(
            state = detailState,
            close = {
                closeDetails()
            },
            onEditClick = onEditClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("MagicNumber")
@Composable
@PreviewCommonScreenConfig
private fun OverviewWithDetailScreenPreview() {
    AppTheme {
        val description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
            "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
            "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
            "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
            "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, " +
            "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
            "ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
            "At vero eos et accusam et justo duo dolores et ea rebum. " +
            "Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet."
        val currency = Currency.getInstance("EUR")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val periodSubscriptions = buildList {
            repeat(5) { id ->
                SubscriptionWithPeriodInfo(
                    subscription = Subscription(
                        SubscriptionId(id.toLong()),
                        "Name",
                        description,
                        paymentInfo = PaymentInfo(
                            Price(99.99, currency),
                            today,
                            PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                        ),
                    ),
                    periodPrice = Price(99.99, currency),
                    nextPaymentDate = LocalDate(2023, 12, 12),
                ).also { add(it) }
            }
        }.toImmutableList()
        OverviewScreenWithDetails(
            overviewState = OverviewState(
                detailId = SubscriptionId(0),
                periodSubscriptions = periodSubscriptions,
                periodPrices = persistentListOf(
                    Price(99.99, currency),
                    Price(99.99, Currency.getInstance("USD")),
                    Price(99.99, Currency.getInstance("GBP")),
                ),
            ),
            detailState = DetailState(
                subscription = Subscription(
                    SubscriptionId(0),
                    "Name",
                    description,
                    paymentInfo = PaymentInfo(
                        Price(99.99, currency),
                        today,
                        PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                    ),
                ),
            ),
            onSubscriptionClicked = {},
            onEditClick = {},
            closeDetails = {},
        )
    }
}
