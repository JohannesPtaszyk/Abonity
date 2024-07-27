package dev.pott.abonity.feature.subscription.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.ads.AdCard
import dev.pott.abonity.core.ui.components.ads.AdId
import dev.pott.abonity.core.ui.components.dialog.DeleteReassuranceDialog
import dev.pott.abonity.core.ui.components.navigation.BackButton
import dev.pott.abonity.core.ui.components.subscription.FormattedDate
import dev.pott.abonity.core.ui.components.subscription.FormattedPrice
import dev.pott.abonity.core.ui.components.subscription.categories.Categories
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.collections.immutable.toImmutableList
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
    onDeleteClick: (SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val subscription = state.subscription
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDeleteDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Crossfade(
                        targetState = subscription?.name.orEmpty(),
                        label = "title_animation",
                    ) { name ->
                        Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                navigationIcon = { BackButton(close) },
                actions = {
                    DetailActions(
                        subscription = subscription,
                        onEditClick = onEditClick,
                        onDeleteClick = { showDeleteDialog = true },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Crossfade(
            targetState = state.subscription == null,
            label = "content_subscription_animation",
            modifier = Modifier.fillMaxSize(),
        ) { isLoading ->
            if (isLoading) {
                DetailLoadingContent(Modifier.fillMaxSize())
            } else {
                subscription?.let {
                    if (showDeleteDialog) {
                        DeleteReassuranceDialog(
                            subscription = subscription,
                            onConfirm = { onDeleteClick(subscription.id) },
                            onDismiss = { showDeleteDialog = false },
                        )
                    }
                    DetailLoadedContent(
                        it,
                        state,
                        paddingValues,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailActions(
    subscription: Subscription?,
    onEditClick: (SubscriptionId) -> Unit,
    onDeleteClick: (SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        subscription != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row {
            subscription?.let {
                IconButton(onClick = { onEditClick(subscription.id) }) {
                    Icon(
                        painter = rememberVectorPainter(image = AppIcons.Edit),
                        contentDescription = stringResource(
                            id = R.string.subscription_detail_edit_label,
                            subscription.name,
                        ),
                    )
                }
                IconButton(onClick = { onDeleteClick(subscription.id) }) {
                    Icon(
                        painter = rememberVectorPainter(image = AppIcons.Delete),
                        contentDescription = stringResource(
                            id = R.string.subscription_detail_delete_label,
                            subscription.name,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailLoadedContent(
    subscription: Subscription,
    state: DetailState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues,
    ) {
        item {
            PaymentInfoCard(
                subscription.paymentInfo,
                Modifier.padding(horizontal = 16.dp),
            )
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
        item {
            ListItem(
                headlineContent = {
                    Text(text = subscription.name)
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_detail_name_label))
                },
            )
        }

        if (subscription.categories.isNotEmpty()) {
            item {
                ListItem(
                    headlineContent = {
                        Categories(
                            categories = subscription.categories.toImmutableList(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    overlineContent = {
                        Text(
                            text = stringResource(
                                id = R.string.subscription_detail_categories_label,
                            ),
                        )
                    },
                )
            }
        }

        state.subscription?.description?.let { description ->
            item {
                ListItem(
                    headlineContent = {
                        Text(text = description)
                    },
                    overlineContent = {
                        Text(
                            text = stringResource(
                                id = R.string.subscription_detail_description_label,
                            ),
                        )
                    },
                )
            }
        }
        item {
            ListItem(
                headlineContent = {
                    FormattedDate(date = subscription.paymentInfo.firstPayment)
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_first_payment_date_label))
                },
            )
        }
        state.nextPayment?.let { nextPayment ->
            item {
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
        }
        item {
            ListItem(
                headlineContent = {
                    Text(text = subscription.id.value.toString())
                },
                overlineContent = {
                    Text(text = stringResource(id = R.string.subscription_id_label))
                },
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            AdCard(adId = AdId.DETAILS_BANNER)
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
                val period = when (paymentType.period) {
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
                            id = R.plurals.payment_per_years,
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
                    categories = listOf(Category(name = "Category")),
                ),
            ),
            close = {
                // Close Screen
            },
            onEditClick = {
                // Open Edit Screen
            },
            onDeleteClick = {
                // Delete
            },
        )
    }
}
