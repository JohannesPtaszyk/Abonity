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
    private val dao: SubscriptionDao,
) : SubscriptionLocalDataSource {

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription {
        val id = dao.upsertSubscription(subscription.toEntity())
        return subscription.copy(id = SubscriptionId(id))
    }

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

    private fun Subscription.toEntity(): SubscriptionEntity {
        val paymentType = paymentInfo.type
        val (period, periodCount) = if (paymentType is PaymentType.Periodic) {
            paymentType.period.toEntity() to paymentType.periodCount
        } else {
            null to null
        }
        return SubscriptionEntity(
            id.id,
            name,
            description,
            paymentInfo.price.value,
            paymentInfo.price.currency.currencyCode,
            paymentInfo.firstPayment.toString(),
            paymentType.toEntity(),
            periodCount,
            period,
        )
    }

    private fun SubscriptionEntity.toDomain(): Subscription {
        val currency = Currency.getInstance(currency)
        val price = Price(price, currency)
        val firstPayment = LocalDate.parse(firstPaymentLocalDate)
        val paymentType =
            paymentType.toDomain(
                periodCount = periodCount,
                period = period,
            )
        val paymentInfo =
            PaymentInfo(
                price = price,
                firstPayment = firstPayment,
                type = paymentType,
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
        period: LocalPaymentPeriod?,
    ): PaymentType {
        return when (this) {
            LocalPaymentType.ONE_TIME -> PaymentType.OneTime
            LocalPaymentType.PERIODICALLY -> {
                val nonNullPeriodCount = checkNotNull(periodCount) {
                    "Period count must never be null for periodic payments"
                }
                val localPaymentPeriod = checkNotNull(period) {
                    "Period must never be null for periodic payments"
                }
                PaymentType.Periodic(nonNullPeriodCount, localPaymentPeriod.toDomain())
            }
        }
    }

    private fun PaymentType.toEntity() =
        when (this) {
            PaymentType.OneTime -> LocalPaymentType.ONE_TIME
            is PaymentType.Periodic -> LocalPaymentType.PERIODICALLY
        }

    private fun LocalPaymentPeriod.toDomain() =
        when (this) {
            LocalPaymentPeriod.DAYS -> PaymentPeriod.DAYS
            LocalPaymentPeriod.WEEKS -> PaymentPeriod.WEEKS
            LocalPaymentPeriod.MONTHS -> PaymentPeriod.MONTHS
            LocalPaymentPeriod.YEARS -> PaymentPeriod.YEARS
        }

    private fun PaymentPeriod.toEntity(): LocalPaymentPeriod =
        when (this) {
            PaymentPeriod.DAYS -> LocalPaymentPeriod.DAYS
            PaymentPeriod.WEEKS -> LocalPaymentPeriod.WEEKS
            PaymentPeriod.MONTHS -> LocalPaymentPeriod.MONTHS
            PaymentPeriod.YEARS -> LocalPaymentPeriod.YEARS
        }
}
