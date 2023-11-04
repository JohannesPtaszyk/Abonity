package dev.pott.abonity.feature.subscription.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.BackButton
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.feature.subscription.components.FormattedPrice
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onBackClick() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    DetailScreen(
        state = state,
        close = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(state: DetailState, close: () -> Unit, modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.subscription?.name.orEmpty())
                },
                navigationIcon = {
                    BackButton(close)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val subscription = state.subscription ?: return@Scaffold
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(paddingValues),
        ) {
            PaymentInfoCard(
                subscription.paymentInfo,
                Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun PaymentInfoCard(paymentInfo: PaymentInfo, modifier: Modifier = Modifier) {
    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .aspectRatio(2f),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            FormattedPrice(
                price = paymentInfo.price,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
            )
            val paymentType = paymentInfo.type
            if (paymentType is PaymentType.Periodic) {
                val period =
                    when (paymentType.period) {
                        PaymentPeriod.DAYS ->
                            pluralStringResource(
                                id = R.plurals.payment_per_days,
                                count = paymentType.periodCount,
                                paymentType.periodCount,
                            )

                        PaymentPeriod.WEEKS ->
                            pluralStringResource(
                                id = R.plurals.payment_per_weeks,
                                count = paymentType.periodCount,
                                paymentType.periodCount,
                            )

                        PaymentPeriod.MONTHS ->
                            pluralStringResource(
                                id = R.plurals.payment_per_months,
                                count = paymentType.periodCount,
                                paymentType.periodCount,
                            )

                        PaymentPeriod.YEARS ->
                            pluralStringResource(
                                id = R.plurals.payment_per_weeks,
                                count = paymentType.periodCount,
                                paymentType.periodCount,
                            )
                    }
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = period,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
@PreviewCommonScreenConfig
private fun DetailScreenPreview() {
    AppTheme {
        DetailScreen(
            state = DetailState(
                subscription = Subscription(
                    SubscriptionId(0),
                    "Name",
                    "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                        "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                        "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
                        "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
                        "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, " +
                        "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
                        "ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
                        "At vero eos et accusam et justo duo dolores et ea rebum. " +
                        "Stet clita kasd gubergren, no sea takimata sanctus est " +
                        "Lorem ipsum dolor sit amet.",
                    paymentInfo = PaymentInfo(
                        Price(99.99, Currency.getInstance("EUR")),
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                    ),
                ),
            ),
            close = {
                // Close Screen
            },
        )
    }
}
