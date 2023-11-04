package dev.pott.abonity.feature.subscription.overview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.DisplayFeature
import co.touchlab.kermit.Logger
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppTheme
import dev.pott.abonity.core.ui.util.getActivity
import dev.pott.abonity.feature.subscription.detail.DetailScreen
import dev.pott.abonity.feature.subscription.detail.DetailState
import dev.pott.abonity.feature.subscription.detail.DetailViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Currency

private const val TWO_PANE_FRACTION = 0.5f

@Composable
fun OverviewScreenWithDetails(
    overviewViewModel: OverviewViewModel,
    detailViewModel: DetailViewModel,
) {
    val activity = LocalContext.current.getActivity()
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = overviewState.detailId != null) {
        Logger.i("Consume details ${overviewState.detailId}")
        overviewViewModel.consumeDetails()
    }
    LaunchedEffect(overviewState.detailId) {
        detailViewModel.setId(overviewState.detailId)
    }
    OverViewScreenWithDetails(
        overviewState,
        detailState,
        overviewViewModel::openDetails,
        overviewViewModel::consumeDetails,
        calculateDisplayFeatures(activity),
    )
}

@Composable
private fun OverViewScreenWithDetails(
    overviewState: OverviewState,
    detailState: DetailState,
    onSubscriptionClicked: (id: SubscriptionId) -> Unit,
    closeDetails: () -> Unit,
    displayFeatures: List<DisplayFeature>,
) {
    TwoPane(
        first = {
            OverviewScreen(
                state = overviewState,
                onSubscriptionClick = onSubscriptionClicked,
            )
        },
        second = {
            AnimatedVisibility(
                label = "Detail animated visibility",
                visible = detailState.subscription?.id != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.systemBarsPadding(),
            ) {
                DetailScreen(
                    state = detailState,
                    close = closeDetails,
                )
            }
        },
        strategy = HorizontalTwoPaneStrategy(TWO_PANE_FRACTION),
        displayFeatures = displayFeatures,
        foldAwareConfiguration = FoldAwareConfiguration.HorizontalFoldsOnly,
    )
}

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
                SelectableSubscriptionWithPeriodPrice(
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
                    false,
                ).also { add(it) }
            }
        }
        OverViewScreenWithDetails(
            overviewState = OverviewState(
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
            {},
            {},
            listOf(),
        )
    }
}
