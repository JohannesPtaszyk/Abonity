package dev.pott.abonity.feature.subscription.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.navigation.BackButton
import dev.pott.abonity.core.ui.components.subscription.FormattedDate
import dev.pott.abonity.core.ui.components.subscription.FormattedPrice
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailState,
    close: () -> Unit,
    onEditClick: (SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                actions = {
                    state.subscription?.let { subscription ->
                        IconButton(onClick = { onEditClick(subscription.id) }) {
                            Icon(
                                painter = rememberVectorPainter(image = AppIcons.Edit),
                                contentDescription = stringResource(
                                    id = R.string.subscription_detail_edit_label,
                                    subscription.name,
                                ),
                            )
                        }
                    }
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
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = subscription.name)
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_detail_name_label))
                },
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = subscription.description)
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_detail_description_label))
                },
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    FormattedDate(date = subscription.paymentInfo.firstPayment)
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_first_payment_date_label))
                },
            )
            state.nextPayment?.let { nextPayment ->
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        FormattedDate(date = nextPayment)
                    },
                    overlineContent = {
                        Text(
                            text = stringResource(
                                id = R.string.subscription_next_payment_date_label,
                            ),
                        )
                    },
                )
            }
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = subscription.id.value.toString())
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_id_label))
                },
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

@Suppress("MagicNumber")
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
            onEditClick = {
                // Open Edit Screen
            },
        )
    }
}
