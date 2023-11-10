package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.feature.subscription.components.PeriodOverviewCard
import dev.pott.abonity.feature.subscription.components.SubscriptionCard
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel,
    openDetails: (id: SubscriptionId) -> Unit,
    openAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val detailId = state.detailId
    LaunchedEffect(detailId) {
        if (detailId == null) return@LaunchedEffect
        openDetails(detailId)
    }

    OverviewScreen(state, viewModel::openDetails, openAdd, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    state: OverviewState,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.overview_screen_title))
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Add),
                    // TODO Add content description
                    contentDescription = null,
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                PeriodOverviewCard(
                    state.periodPrices,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            items(
                state.periodSubscriptions,
                key = { it.subscription.id.id },
                contentType = { "Subscription Card" },
            ) { subscriptionWithPeriodPrice ->
                SubscriptionCard(
                    subscriptionWithPeriodPrice.subscription,
                    subscriptionWithPeriodPrice.periodPrice,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubscriptionClick(subscriptionWithPeriodPrice.subscription.id)
                    },
                    isSelected = subscriptionWithPeriodPrice.isSelected,
                )
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
@PreviewCommonScreenConfig
private fun OverviewScreenPreview() {
    val description =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
            "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
            "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
            "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
            "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, " +
            "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
            "ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
            "At vero eos et accusam et justo duo dolores et ea rebum. " +
            "Stet clita kasd gubergren, no sea takimata sanctus est " +
            "Lorem ipsum dolor sit amet."

    AppTheme {
        OverviewScreen(
            state = OverviewState(
                periodSubscriptions = buildList {
                    repeat(5) { id ->
                        SelectableSubscriptionWithPeriodPrice(
                            subscription = Subscription(
                                SubscriptionId(id.toLong()),
                                "Name",
                                description,
                                paymentInfo = PaymentInfo(
                                    Price(99.99, Currency.getInstance("EUR")),
                                    Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                                    PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                                ),
                            ),
                            periodPrice = Price(99.99, Currency.getInstance("EUR")),
                            false,
                        ).also { add(it) }
                    }
                },
            ),
            onSubscriptionClick = {
                // On Subscription click
            },
            onAddClick = {
                // On Add click
            },
        )
    }
}
