package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailState
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
    onFilterItemSelected: (item: SubscriptionFilterItem) -> Unit,
    onEditClicked: (id: SubscriptionId) -> Unit,
    onDeleteClicked: (id: SubscriptionId) -> Unit,
    closeDetails: () -> Unit,
    onOpenCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        OverviewScreen(
            state = overviewState,
            onSubscriptionClick = onSubscriptionClicked,
            onFilterItemSelected = onFilterItemSelected,
            onSwipeToDelete = onDeleteClicked,
            listState = listState,
            onOpenCategoriesClick = onOpenCategoriesClick,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        if ((overviewState as? OverviewState.Loaded)?.detailId == null) {
            EmptyDetails(Modifier.fillMaxHeight().weight(1f))
        } else {
            DetailScreen(
                state = detailState,
                close = {
                    closeDetails()
                },
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EmptyDetails(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ghost),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.subscription_detail_empty_label),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
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
                        categories = listOf(Category(name = "Category")),
                    ),
                    periodPrice = Price(99.99, currency),
                    nextPaymentDate = LocalDate(2023, 12, 12),
                ).also { add(it) }
            }
        }.toImmutableList()
        OverviewScreenWithDetails(
            overviewState = OverviewState.Loaded(
                detailId = SubscriptionId(0),
                subscriptions = periodSubscriptions,
                filter = SubscriptionFilter(
                    emptyList(),
                    emptyList(),
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
                    categories = listOf(Category(name = "Category")),
                ),
            ),
            onSubscriptionClicked = {},
            onFilterItemSelected = {},
            onEditClicked = {},
            onDeleteClicked = {},
            closeDetails = {},
            onOpenCategoriesClick = {},
        )
    }
}
