package dev.pott.abonity.feature.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.subscription.SubscriptionCard
import dev.pott.abonity.core.ui.components.text.SectionHeader
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.core.ui.util.plus
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    openDetails: (id: SubscriptionId) -> Unit,
    openSubscriptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.selectedId) {
        val selectedId = state.selectedId ?: return@LaunchedEffect
        openDetails(selectedId)
        viewModel.consumeSelectedId()
    }
    HomeScreen(state, viewModel::select, openSubscriptions, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: DashboardState,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    onOpenSubscriptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home_screen_title))
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                SectionHeader(
                    modifier = Modifier.fillMaxWidth(),
                    action = {
                        TextButton(onClick = onOpenSubscriptionsClick) {
                            Text(text = stringResource(id = R.string.home_btn_open_subscriptions))
                        }
                    },
                ) {
                    Text(text = stringResource(id = R.string.home_upcomming_subscriptions_label))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(
                state.upcomingSubscriptions,
                key = { _, item -> item.subscription.id.value },
                contentType = { _, _ -> "subscription_card" },
            ) { i, subscription ->
                SubscriptionCard(
                    subscription.subscription,
                    subscription.periodPrice,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubscriptionClick(subscription.subscription.id)
                    },
                    isSelected = subscription.subscription.id == state.selectedId,
                )
                if (i != state.upcomingSubscriptions.lastIndex) {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
@PreviewCommonScreenConfig
private fun HomeScreenPreview() {
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
        HomeScreen(
            state = DashboardState(
                upcomingSubscriptions = buildList {
                    repeat(5) { id ->
                        SubscriptionWithPeriodInfo(
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
                            nextPaymentDate = LocalDate(2023, 12, 12),
                        ).also { add(it) }
                    }
                }.toImmutableList(),
            ),
            onSubscriptionClick = {
                // On Subscription click
            },
            onOpenSubscriptionsClick = {
                // On Open Subscription Click
            },
        )
    }
}
