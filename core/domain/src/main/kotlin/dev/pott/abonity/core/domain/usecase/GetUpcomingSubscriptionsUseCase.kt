package dev.pott.abonity.core.domain.usecase

import dev.pott.abonity.core.domain.PaymentInfoCalculator
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUpcomingSubscriptionsUseCase @Inject constructor(
    private val calculator: PaymentInfoCalculator,
    private val getSubscriptionsWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
) {
    operator fun invoke(period: PaymentPeriod): Flow<List<SubscriptionWithPeriodInfo>> {
        return getSubscriptionsWithPeriodPrice().map { subscriptions ->
            subscriptions.filter {
                val firstPayment = it.subscription.paymentInfo.firstPayment
                val paymentType = it.subscription.paymentInfo.type
                val paymentsInPeriod = calculator.getPaymentCountForCurrentPeriod(
                    firstPayment,
                    period,
                    paymentType,
                )
                paymentsInPeriod > 0
            }
        }
    }
}
