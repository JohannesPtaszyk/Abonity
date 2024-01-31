package dev.pott.abonity.feature.home

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.entity.subscription.UpcomingSubscriptions
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.subscription.SubscriptionCard
import dev.pott.abonity.core.ui.components.text.SectionHeader
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.feature.home.components.NoSubscriptionTeaser
import dev.pott.abonity.feature.home.components.NoUpcomingSubscriptionTeaser
import dev.pott.abonity.feature.home.components.NotificationPermissionTeaser
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    openDetails: (id: SubscriptionId) -> Unit,
    openSubscriptions: () -> Unit,
    openNotificationSettings: () -> Unit,
    openAddScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        val selectedId = (state as? DashboardState.Loaded)?.selectedId ?: return@LaunchedEffect
        openDetails(selectedId)
        viewModel.consumeSelectedId()
    }
    DashboardScreen(
        state,
        viewModel::select,
        openSubscriptions,
        openNotificationSettings,
        viewModel::closeNotificationTeaser,
        openAddScreen,
        modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    onOpenSubscriptionsClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onCloseNotificationTeaserClick: (shouldNotShowAgain: Boolean) -> Unit,
    onAddNewSubscriptionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home_screen_title))
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        AnimatedContent(
            targetState = state,
            contentKey = {
                when (state) {
                    is DashboardState.Loaded -> "Loaded"
                    DashboardState.Loading -> "Loading"
                }
            },
            modifier = Modifier.padding(paddingValues),
            label = "content_animation",
        ) { dashboardState ->
            when (dashboardState) {
                is DashboardState.Loaded -> {
                    LoadedContent(
                        onOpenNotificationSettingsClick,
                        dashboardState,
                        onCloseNotificationTeaserClick,
                        onOpenSubscriptionsClick,
                        onSubscriptionClick,
                        onAddNewSubscriptionClicked,
                        scrollBehavior.nestedScrollConnection,
                    )
                }

                DashboardState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class,
)
private fun LoadedContent(
    onOpenNotificationSettingsClick: () -> Unit,
    dashboardState: DashboardState.Loaded,
    onCloseNotificationTeaserClick: (shouldNotShowAgain: Boolean) -> Unit,
    onOpenSubscriptionsClick: () -> Unit,
    onSubscriptionClick: (id: SubscriptionId) -> Unit,
    onAddNewSubscriptionClicked: () -> Unit,
    nestedScrollConnection: NestedScrollConnection,
) {
    var showNotificationDialog by remember { mutableStateOf(false) }
    val notificationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(
                permission = Manifest.permission.POST_NOTIFICATIONS,
                onPermissionResult = { isGranted ->
                    if (isGranted) return@rememberPermissionState
                    showNotificationDialog = true
                },
            )
        } else {
            null
        }

    if (showNotificationDialog) {
        NotificationPermissionDialog(
            onDismissRequest = { showNotificationDialog = false },
            onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.nestedScroll(nestedScrollConnection),
    ) {
        val shouldShowNotificationTeaser = dashboardState.shouldShowNotificationTeaser
        if (showNotificationTeaser(
                notificationPermissionState,
                shouldShowNotificationTeaser,
            )
        ) {
            item(
                key = "notification_permission_teaser",
                contentType = "notification_permission_teaser",
            ) {
                NotificationPermissionTeaser(
                    notificationPermissionState,
                    onCloseClicked = onCloseNotificationTeaserClick,
                    modifier = Modifier.animateItemPlacement(),
                )
            }
        }
        item(
            key = "header_upcoming_subscriptions",
            contentType = "section_header",
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                action = {
                    TextButton(onClick = onOpenSubscriptionsClick) {
                        Text(stringResource(R.string.home_btn_open_subscriptions))
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.home_upcoming_subscriptions_label))
            }
        }
        val upcoming = dashboardState.upcomingSubscriptions
        when {
            upcoming.subscriptions.isEmpty() && upcoming.hasAnySubscriptions -> {
                item(
                    key = "no_upcoming_subscription_teaser",
                    contentType = "no_upcoming_subscription_teaser",
                ) {
                    NoUpcomingSubscriptionTeaser(
                        onAddNewSubscriptionClicked = onAddNewSubscriptionClicked,
                        period = dashboardState.upcomingSubscriptions.period,
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                    )
                }
            }
            upcoming.subscriptions.isEmpty() && !upcoming.hasAnySubscriptions -> {
                item(
                    key = "no_subscription_teaser",
                    contentType = "no_subscription_teaser",
                ) {
                    NoSubscriptionTeaser(
                        onAddNewSubscriptionClicked = onAddNewSubscriptionClicked,
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                    )
                }
            }
        }
        itemsIndexed(
            dashboardState.upcomingSubscriptions.subscriptions,
            key = { _, item -> item.subscription.id.value },
            contentType = { _, _ -> "subscription_card" },
        ) { index, subscription ->
            SubscriptionCard(
                subscription.subscription,
                subscription.periodPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                onClick = {
                    onSubscriptionClick(subscription.subscription.id)
                },
                isSelected = subscription.subscription.id == dashboardState.selectedId,
            )
            if (index != dashboardState.upcomingSubscriptions.subscriptions.lastIndex) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalContracts::class)
private fun showNotificationTeaser(
    notificationPermissionState: PermissionState?,
    shouldShowNotificationTeaser: Boolean,
): Boolean {
    contract {
        returns(true) implies (notificationPermissionState != null)
    }
    return (
        notificationPermissionState != null &&
            !notificationPermissionState.status.isGranted &&
            shouldShowNotificationTeaser
        )
}

@Composable
private fun NotificationPermissionDialog(
    onDismissRequest: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.notification_permission_dialog_title))
        },
        icon = {
            Icon(painter = rememberVectorPainter(image = AppIcons.Notification), null)
        },
        text = {
            Text(
                text = stringResource(id = R.string.notification_permission_dialog_text),
                textAlign = TextAlign.Center,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onOpenNotificationSettingsClick()
                },
            ) {
                Text(stringResource(R.string.notification_permission_dialog_settings_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.dialog_btn_dismiss_default))
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Suppress("MagicNumber")
@Composable
@PreviewCommonScreenConfig
private fun DashboardScreenPreview() {
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
        DashboardScreen(
            state = DashboardState.Loaded(
                UpcomingSubscriptions(
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
                                ),
                                periodPrice = Price(99.99, Currency.getInstance("EUR")),
                                nextPaymentDate = LocalDate(2023, 12, 12),
                            ).also { add(it) }
                        }
                    },
                    hasAnySubscriptions = true,
                    period = PaymentPeriod.MONTHS,
                ),
                selectedId = null,
                shouldShowNotificationTeaser = true,
            ),
            onSubscriptionClick = {
                // On Subscription click
            },
            onOpenSubscriptionsClick = {
                // On Open Subscription Click
            },
            onOpenNotificationSettingsClick = {
                // On Open Notification Settings Click
            },
            onCloseNotificationTeaserClick = {
                // On Close Notification Teaser Click
            },
            onAddNewSubscriptionClicked = {
                // On Add New Subscription Clicked
            },
        )
    }
}
