package dev.pott.abonity.core.local

import dev.pott.abonity.core.domain.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.core.local.db.SubscriptionDao
import dev.pott.abonity.core.local.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.db.entities.LocalPaymentType
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import java.util.Currency
import javax.inject.Inject

class RoomSubscriptionDataSource @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionLocalDataSource {
    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return dao.getSubscriptionsFlow().map { subscriptions ->
            subscriptions.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription> {
        return dao.getSubscriptionFlow(subscriptionId.id).map { it.toDomain() }
    }

    private fun SubscriptionEntity.toDomain(): Subscription {
        val currency = Currency.getInstance(currency)
        val price = Price(price, currency)
        val firstPayment = LocalDate.parse(firstPaymentLocalDate)
        val paymentType = paymentType.toDomain(
            periodCount = periodCount,
            period = period,
        )
        val paymentInfo = PaymentInfo(
            price = price,
            firstPayment = firstPayment,
            type = paymentType
        )

        return Subscription(
            id = SubscriptionId(id),
            name = name,
            description = description,
            paymentInfo = paymentInfo,
        )
    }

    private fun LocalPaymentType.toDomain(
        periodCount: Int?,
        period: LocalPaymentPeriod?
    ): PaymentType {
        return when (this) {
            LocalPaymentType.ONE_TIME -> {
                PaymentType.OneTime
            }

            LocalPaymentType.PERIODICALLY -> {
                val nonNullPeriodCount = checkNotNull(periodCount) {
                    "Period count must never be null for periodic payments"
                }
                val localPaymentPeriod = checkNotNull(period) {
                    "Period must never be null for periodic payments"
                }
                val paymentPeriod = when (localPaymentPeriod) {
                    LocalPaymentPeriod.DAYS -> PaymentPeriod.DAYS
                    LocalPaymentPeriod.WEEKS -> PaymentPeriod.WEEKS
                    LocalPaymentPeriod.MONTHS -> PaymentPeriod.MONTHS
                    LocalPaymentPeriod.YEARS -> PaymentPeriod.YEARS
                }
                PaymentType.Periodic(nonNullPeriodCount, paymentPeriod)
            }
        }
    }
}
