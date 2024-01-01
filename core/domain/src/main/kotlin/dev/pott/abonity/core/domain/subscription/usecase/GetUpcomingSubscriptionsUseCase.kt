package dev.pott.abonity.core.domain.subscription.usecase

import dev.pott.abonity.core.domain.subscription.getLastDayOfCurrentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetUpcomingSubscriptionsUseCase @Inject constructor(
    private val clock: Clock,
    private val getSubscriptionsWithPeriodPrice: GetSubscriptionsWithPeriodPrice,
) {
    operator fun invoke(period: PaymentPeriod): Flow<List<SubscriptionWithPeriodInfo>> {
        return getSubscriptionsWithPeriodPrice().map { subscriptions ->
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            subscriptions.filter {
                val lastDayOfCurrentPeriod = today.getLastDayOfCurrentPeriod(period)
                it.nextPaymentDate in today..lastDayOfCurrentPeriod
            }
        }
    }
}
