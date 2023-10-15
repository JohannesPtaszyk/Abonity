package dev.pott.abonity.core.local

import dev.pott.abonity.core.domain.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.HexColor
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.local.db.SubscriptionDao
import dev.pott.abonity.core.local.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.db.entities.LocalPaymentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import java.util.Currency
import javax.inject.Inject

class RoomSubscriptionDataSource @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionLocalDataSource {
    override fun getSubscriptionFlow(): Flow<List<Subscription>> {
        return dao.getSubscriptionFlow().map { subscriptions ->
            subscriptions.map { entity ->
                val currency = Currency.getInstance(entity.currency)
                val price = Price(entity.price, currency)
                val firstPayment = LocalDate.parse(entity.firstPaymentLocalDate)
                val paymentType = entity.paymentType.toDomain(
                    periodCount = entity.periodCount,
                    period = entity.period,
                )
                val paymentInfo = PaymentInfo(
                    price = price,
                    firstPayment = firstPayment,
                    type = paymentType
                )

                Subscription(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    paymentInfo = paymentInfo,
                    color = HexColor(entity.color),
                )
            }
        }
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
