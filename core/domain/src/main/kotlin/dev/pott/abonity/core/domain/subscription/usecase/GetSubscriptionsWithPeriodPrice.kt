package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSubscriptionsWithPeriodPrice @Inject constructor(
    private val repository: SubscriptionRepository,
    private val infoCalculator: PaymentInfoCalculator,
) {
    operator fun invoke(): Flow<List<SubscriptionWithPeriodInfo>> {
        return repository.getSubscriptionsFlow().map { subscriptions ->
            subscriptions.map { subscription -> map(subscription) }
        }
    }

    private fun map(subscription: Subscription): SubscriptionWithPeriodInfo {
        val nextPaymentDate = when (val type = subscription.paymentInfo.type) {
            PaymentType.OneTime -> subscription.paymentInfo.firstPayment
            is PaymentType.Periodic -> {
                infoCalculator.getPaymentDatesForCurrentPeriod(
                    subscription.paymentInfo.firstPayment,
                    PaymentPeriod.MONTHS,
                    type,
                ).firstOrNull() ?: infoCalculator.getNextDateByType(type)
            }
        }

        val totalPrice = infoCalculator.getTotalPriceForPeriod(
            paymentInfo = subscription.paymentInfo,
            targetPeriod = PaymentPeriod.MONTHS,
        )

        return SubscriptionWithPeriodInfo(
            subscription,
            totalPrice,
            nextPaymentDate,
        )
    }
}
