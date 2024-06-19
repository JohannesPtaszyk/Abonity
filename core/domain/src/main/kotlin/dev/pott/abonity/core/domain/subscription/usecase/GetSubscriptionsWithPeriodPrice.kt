package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.common.injection.Dispatcher
import dev.pott.abonity.common.injection.Dispatcher.Type.DEFAULT
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSubscriptionsWithPeriodPrice @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val infoCalculator: PaymentInfoCalculator,
    @Dispatcher(DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(): Flow<List<SubscriptionWithPeriodInfo>> =
        combine(
            subscriptionRepository.getSubscriptionsFlow(),
            settingsRepository.getSettingsFlow(),
        ) { subscriptions, settings ->
            subscriptions.map { subscription -> map(subscription, settings.period) }
        }.flowOn(defaultDispatcher)

    private fun map(subscription: Subscription, period: PaymentPeriod): SubscriptionWithPeriodInfo {
        val nextPaymentDate = when (val type = subscription.paymentInfo.type) {
            PaymentType.OneTime -> subscription.paymentInfo.firstPayment
            is PaymentType.Periodic -> {
                infoCalculator.getPaymentDatesForCurrentPeriod(
                    subscription.paymentInfo.firstPayment,
                    period,
                    type,
                ).firstOrNull() ?: infoCalculator.getNextDateByType(type)
            }
        }

        val totalPrice = infoCalculator.getTotalPriceForPeriod(
            paymentInfo = subscription.paymentInfo,
            targetPeriod = period,
        )

        return SubscriptionWithPeriodInfo(
            subscription,
            totalPrice,
            nextPaymentDate,
        )
    }
}
