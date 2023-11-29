package dev.pott.abonity.feature.subscription.overview

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.ThreePaneScaffoldState
import androidx.compose.material3.adaptive.calculateListDetailPaneScaffoldState
import androidx.compose.runtime.Composable
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OverviewScreenWithDetails(
    overviewState: OverviewState,
    detailState: DetailState,
    onSubscriptionClicked: (id: SubscriptionId) -> Unit,
    onEditClick: (id: SubscriptionId) -> Unit,
    closeDetails: () -> Unit,
    scaffoldState: ThreePaneScaffoldState = calculateListDetailPaneScaffoldState(),
) {
    ListDetailPaneScaffold(
        scaffoldState = scaffoldState,
        listPane = {
            OverviewScreen(
                state = overviewState,
                onSubscriptionClick = {
                    onSubscriptionClicked(it)
                },
            )
        },
        detailPane = {
            if (overviewState.detailId != null) {
                DetailScreen(
                    state = detailState,
                    close = {
                        closeDetails()
                    },
                    onEditClick = onEditClick,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
