package dev.pott.abonity.feature.subscription.detail

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.BackButton
import dev.pott.abonity.feature.subscription.components.FormattedPrice
import dev.pott.abonity.feature.subscription.components.PeriodicPriceInfo

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    close: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DetailScreen(
        state = state,
        close = close,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailState,
    close: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.subscription?.name.orEmpty())
                },
                navigationIcon = {
                    BackButton(close)
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val subscription = state.subscription ?: return@Scaffold
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            PaymentInfoCard(subscription.paymentInfo)

        }
    }
}

@Composable
private fun PaymentInfoCard(paymentInfo: PaymentInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FormattedPrice(
                price = paymentInfo.price,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
            )
            val paymentType = paymentInfo.type
            if (paymentType is PaymentType.Periodic) {
                val period = when (paymentType.period) {
                    PaymentPeriod.DAYS -> pluralStringResource(
                        id = R.plurals.payment_per_days,
                        count = paymentType.periodCount,
                        paymentType.periodCount,
                    )

                    PaymentPeriod.WEEKS -> pluralStringResource(
                        id = R.plurals.payment_per_weeks,
                        count = paymentType.periodCount,
                        paymentType.periodCount,
                    )

                    PaymentPeriod.MONTHS -> pluralStringResource(
                        id = R.plurals.payment_per_months,
                        count = paymentType.periodCount,
                        paymentType.periodCount,
                    )

                    PaymentPeriod.YEARS -> pluralStringResource(
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
