package dev.pott.abonity.core.local.subscription.mapper

import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentType

fun LocalPaymentType.toDomain(periodCount: Int?, period: LocalPaymentPeriod?): PaymentType {
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

fun PaymentType.toEntity() =
    when (this) {
        PaymentType.OneTime -> LocalPaymentType.ONE_TIME
        is PaymentType.Periodic -> LocalPaymentType.PERIODICALLY
    }
