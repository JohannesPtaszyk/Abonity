package dev.pott.abonity.core.domain.usecase

import dev.pott.abonity.core.domain.PaymentDateCalculator
import dev.pott.abonity.core.domain.PaymentInfoCalculator
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSubscriptionsWithPeriodPrice @Inject constructor(
    private val repository: SubscriptionRepository,
    private val infoCalculator: PaymentInfoCalculator,
    private val dateCalculator: PaymentDateCalculator,
) {
    operator fun invoke(): Flow<List<SubscriptionWithPeriodInfo>> {
        return repository.getSubscriptionsFlow().map { subscriptions ->
            subscriptions.map { subscription -> map(subscription) }
        }
    }

    private fun map(subscription: Subscription): SubscriptionWithPeriodInfo {
        val nextPaymentDate = when (val type = subscription.paymentInfo.type) {
            PaymentType.OneTime -> subscription.paymentInfo.firstPayment
            is PaymentType.Periodic -> dateCalculator.calculateNextPossiblePaymentDate(type)
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
