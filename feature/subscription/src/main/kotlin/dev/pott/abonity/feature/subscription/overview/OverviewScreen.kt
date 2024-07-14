package dev.pott.abonity.feature.subscription.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
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
import dev.pott.abonity.core.ui.components.dismiss.DeleteDismissBackground
import dev.pott.abonity.core.ui.components.subscription.SubscriptionCard
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilter
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

private const val SUBSCRIPTION_FILTER = "SubscriptionFilter"
private const val SUBSCRIPTION_CARD = "SubscriptionFilter"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    state: OverviewState,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    onFilterItemSelect: (item: SubscriptionFilterItem) -> Unit,
    onSwipeToDelete: (id: SubscriptionId) -> Unit,
    onOpenCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.overview_screen_title)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onOpenCategoriesClick) {
                        Icon(
                            painter = rememberVectorPainter(AppIcons.Categories),
                            contentDescription = "Show Categories",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (state) {
            is OverviewState.Loaded -> {
                LoadedContent(
                    paddingValues,
                    scrollBehavior,
                    listState,
                    state,
                    onFilterItemSelect,
                    onSwipeToDelete,
                    onSubscriptionClick,
                )
            }

            OverviewState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
private fun LoadedContent(
    paddingValues: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState,
    state: OverviewState.Loaded,
    onFilterItemSelect: (item: SubscriptionFilterItem) -> Unit,
    onSwipeToDelete: (id: SubscriptionId) -> Unit,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
) {
    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        state = listState,
    ) {
        item(
            key = SUBSCRIPTION_FILTER,
            contentType = SUBSCRIPTION_FILTER,
        ) {
            SubscriptionFilter(
                state.filter,
                onItemSelect = onFilterItemSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
            )
        }
        items(
            state.subscriptions,
            key = { it.subscription.id.value },
            contentType = { SUBSCRIPTION_CARD },
        ) { subscriptionWithPeriodInfo ->
            SubscriptionCardItem(
                subscriptionWithPeriodInfo,
                state,
                onSwipeToDelete,
                onSubscriptionClick,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .animateItemPlacement(),
            )
        }
    }
}

private const val SWIPE_TO_DELETE_THRESHOLD = 1.75f

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SubscriptionCardItem(
    subscriptionWithPeriodInfo: SubscriptionWithPeriodInfo,
    state: OverviewState.Loaded,
    onSwipeToDelete: (id: SubscriptionId) -> Unit,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = subscriptionWithPeriodInfo.subscription.id == state.detailId
    var swipeToDismissPositionalThreshold by remember { mutableFloatStateOf(0f) }
    val swipeToDismissState = rememberSwipeToDismissBoxState { swipeToDismissPositionalThreshold }
    val coroutineScope = rememberCoroutineScope()

    if (swipeToDismissState.currentValue != SwipeToDismissBoxValue.Settled) {
        AlertDialog(
            icon = {
                Icon(
                    painter = rememberVectorPainter(image = AppIcons.Delete),
                    contentDescription = null,
                )
            },
            title = {
                Text(stringResource(id = R.string.subscription_overview_swipe_delete_dialog_title))
            },
            text = {
                Text(
                    stringResource(
                        id = R.string.subscription_overview_swipe_delete_dialog_text,
                        subscriptionWithPeriodInfo.subscription.name,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onSwipeToDelete(subscriptionWithPeriodInfo.subscription.id) },
                ) {
                    Text(stringResource(id = R.string.dialog_btn_confirm_default))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { coroutineScope.launch { swipeToDismissState.reset() } },
                ) {
                    Text(stringResource(id = R.string.dialog_btn_dismiss_default))
                }
            },
            onDismissRequest = { coroutineScope.launch { swipeToDismissState.reset() } },
        )
    }

    SwipeToDismissBox(
        modifier = modifier
            .clip(CardDefaults.shape)
            .onSizeChanged {
                swipeToDismissPositionalThreshold = it.width.toFloat() / SWIPE_TO_DELETE_THRESHOLD
            },
        state = swipeToDismissState,
        backgroundContent = {
            DeleteDismissBackground(
                dismissState = swipeToDismissState,
                contentDescription = stringResource(
                    id = R.string.subscription_overview_swipe_delete_label,
                    subscriptionWithPeriodInfo.subscription.name,
                ),
            )
        },
        content = {
            SubscriptionCard(
                subscriptionWithPeriodInfo.subscription,
                subscriptionWithPeriodInfo.periodPrice,
                onClick = {
                    onSubscriptionClick(
                        subscriptionWithPeriodInfo.subscription.id,
                    )
                },
                isSelected = isSelected,
                currentPeriod = state.currentPeriod,
            )
        },
    )
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
            state = OverviewState.Loaded(
                subscriptions = buildList {
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
                                categories = listOf(Category(name = "Category")),
                            ),
                            periodPrice = Price(99.99, Currency.getInstance("EUR")),
                            nextPaymentDate = LocalDate(22, 12, 22),
                        ).also { add(it) }
                    }
                }.toImmutableList(),
                filter = SubscriptionFilter(
                    emptyList(),
                    emptyList(),
                ),
                currentPeriod = PaymentPeriod.MONTHS,
            ),
            onSubscriptionClick = {
                // On Subscription click
            },
            onFilterItemSelect = {
                // On Filter Item Selected
            },
            onSwipeToDelete = {
                // On Swipe to delete
            },
            onOpenCategoriesClick = {
                // On Open Categories Click
            },
        )
    }
}
