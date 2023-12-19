package dev.pott.abonity.core.domain.usecase

import dev.pott.abonity.core.domain.PaymentInfoCalculator
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetUpcomingSubscriptionsUseCase @Inject constructor(
    private val clock: Clock,
    private val calculator: PaymentInfoCalculator,
    private val getSubscriptionsWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
) {
    operator fun invoke(period: PaymentPeriod): Flow<List<SubscriptionWithPeriodInfo>> {
        return getSubscriptionsWithPeriodPrice().map { subscriptions ->
            val now = clock.todayIn(TimeZone.currentSystemDefault())
            subscriptions.map {
                val firstPayment = it.subscription.paymentInfo.firstPayment
                val paymentType = it.subscription.paymentInfo.type
                val paymentsInPeriod = calculator.getPaymentDatesForCurrentPeriod(
                    firstPayment,
                    period,
                    paymentType,
                ).filter { paymentDate ->
                    paymentDate > now
                }
                it to paymentsInPeriod
            }.filter {
                it.second.isNotEmpty()
            }.sortedBy {
                it.second.first()
            }.map {
                it.first
            }
        }
    }
}
