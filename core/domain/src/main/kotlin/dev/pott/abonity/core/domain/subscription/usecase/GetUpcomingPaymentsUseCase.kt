package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.common.injection.Dispatcher
import dev.pott.abonity.common.injection.Dispatcher.Type.DEFAULT
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.domain.subscription.getLastDayOfCurrentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.UpcomingPayment
import dev.pott.abonity.core.entity.subscription.UpcomingPayments
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetUpcomingPaymentsUseCase @Inject constructor(
    private val clock: Clock,
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val infoCalculator: PaymentInfoCalculator,
    @Dispatcher(DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<UpcomingPayments> =
        combine(
            subscriptionRepository.getSubscriptionsFlow(),
            settingsRepository.getSettingsFlow(),
        ) { subscriptions, settings ->
            val period = settings.period
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            val lastDayOfCurrentPeriod = today.getLastDayOfCurrentPeriod(settings.period)
            val payments = subscriptions
                .map { subscription -> getUpcomingPayments(subscription, period) }
                .flatten()
                .filter { it.date in today..lastDayOfCurrentPeriod }
                .groupBy { it.date }
                .toSortedMap()
            UpcomingPayments(payments, subscriptions.isNotEmpty(), settings.period)
        }.flowOn(defaultDispatcher)

    private fun getUpcomingPayments(
        subscription: Subscription,
        period: PaymentPeriod,
    ): List<UpcomingPayment> {
        val payments = when (val type = subscription.paymentInfo.type) {
            PaymentType.OneTime -> {
                listOf(subscription.paymentInfo.firstPayment)
            }

            is PaymentType.Periodic -> {
                infoCalculator.getPaymentDatesForCurrentPeriod(
                    subscription.paymentInfo.firstPayment,
                    period,
                    type,
                ).ifEmpty { listOf(infoCalculator.getNextDateByType(type)) }
            }
        }
        return payments.map { UpcomingPayment(subscription, it) }
    }
}
