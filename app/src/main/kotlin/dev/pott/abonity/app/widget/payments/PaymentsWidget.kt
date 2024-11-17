package dev.pott.abonity.app.widget.payments

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.TextAlign
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.pott.abonity.app.MainActivity
import dev.pott.abonity.app.R
import dev.pott.abonity.app.widget.action.LaunchDeeplinkAction
import dev.pott.abonity.app.widget.components.GlanceFilledCard
import dev.pott.abonity.app.widget.components.GlanceFormattedDate
import dev.pott.abonity.app.widget.components.GlanceFormattedPrice
import dev.pott.abonity.app.widget.components.GlanceText
import dev.pott.abonity.app.widget.theme.GlanceWidgetTheme
import dev.pott.abonity.app.widget.theme.LocalGlanceTextStyle
import dev.pott.abonity.core.domain.subscription.usecase.GetUpcomingPaymentsUseCase
import dev.pott.abonity.core.entity.subscription.UpcomingPayment
import dev.pott.abonity.core.entity.subscription.UpcomingPayments
import dev.pott.abonity.core.navigation.Deeplinks

class PaymentsWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PaymentsWidgetEntryPoint {
        fun provideUpcomingPaymentsUseCase(): GetUpcomingPaymentsUseCase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            PaymentsWidgetEntryPoint::class.java,
        )
        provideContent {
            val upcomingPayments by entryPoint.provideUpcomingPaymentsUseCase().invoke()
                .collectAsState(UpcomingPayments.empty())

            GlanceWidgetTheme {
                Box(
                    modifier = GlanceModifier
                        .then(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                GlanceModifier.background(GlanceTheme.colors.background)
                            } else {
                                GlanceModifier.background(
                                    imageProvider = ImageProvider(
                                        R.drawable.glance_rounded_corner_16,
                                    ),
                                    colorFilter = ColorFilter.tint(GlanceTheme.colors.background),
                                )
                            },
                        )
                        .appWidgetBackground()
                        .padding(horizontal = 16.dp),
                ) {
                    if (upcomingPayments.payments.isNotEmpty()) {
                        UpcomingPaymentsLayout(upcomingPayments)
                    } else if (upcomingPayments.hasSubscriptions) {
                        NoUpcomingPaymentsLayout()
                    } else {
                        NoSubscriptionLayout()
                    }
                }
            }
        }
    }

    @Composable
    private fun NoUpcomingPaymentsLayout() {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.padding(16.dp)
                .fillMaxSize()
                .clickable(onClick = actionStartActivity<MainActivity>()),
        ) {
            GlanceText(
                text = LocalContext.current
                    .getString(dev.pott.abonity.core.ui.R.string.payments_widget_no_upcoming_text),
                style = LocalGlanceTextStyle.current.copy(textAlign = TextAlign.Center),
            )
        }
    }

    @Composable
    private fun NoSubscriptionLayout() {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.padding(16.dp).fillMaxSize(),
        ) {
            Button(
                text = LocalContext.current.getString(
                    dev.pott.abonity.core.ui.R.string.no_subscription_teaser_cta,
                ),
                onClick = actionRunCallback<LaunchDeeplinkAction>(
                    parameters = actionParametersOf(
                        LaunchDeeplinkAction.deeplinkKey to Deeplinks.ADD_SUBSCRIPTION,
                    ),
                ),
            )
        }
    }

    @Composable
    private fun UpcomingPaymentsLayout(upcomingPayments: UpcomingPayments) {
        LazyColumn {
            upcomingPayments.payments.entries.forEachIndexed { index, (date, payments) ->
                item {
                    GlanceFormattedDate(
                        date = date,
                        modifier = GlanceModifier.padding(
                            top = 16.dp,
                            bottom = 8.dp,
                        ),
                    )
                }
                itemsIndexed(payments) { i, payment ->
                    Box(
                        modifier = GlanceModifier.padding(
                            bottom = if (i != payments.lastIndex) {
                                16.dp
                            } else {
                                0.dp
                            },
                        ),
                    ) {
                        UpcomingPaymentCard(payment)
                    }
                }
            }
            item { Spacer(modifier = GlanceModifier.height(16.dp)) }
        }
    }

    @Composable
    private fun UpcomingPaymentCard(payment: UpcomingPayment) {
        GlanceFilledCard(
            modifier = GlanceModifier.padding(16.dp),
            onClick = actionRunCallback<LaunchDeeplinkAction>(
                parameters = actionParametersOf(
                    LaunchDeeplinkAction.deeplinkKey to Deeplinks.createSubscriptionDeeplink(
                        payment.subscription.id.value,
                    ),
                ),
            ),
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
            ) {
                GlanceText(
                    text = payment.subscription.name,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight(),
                    style = LocalGlanceTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                    ),
                )
                GlanceFormattedPrice(
                    price = payment.subscription.paymentInfo.price,
                    color = GlanceTheme.colors.primary,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight(),
                    style = LocalGlanceTextStyle.current.copy(
                        textAlign = TextAlign.End,
                    ),
                )
            }
        }
    }
}
