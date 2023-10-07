package dev.pott.abonity.entities

import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType

fun createPeriodicallyPaymentType(
    periodCount: Int = 1,
    period: PaymentPeriod = PaymentPeriod.MONTHS
): PaymentType.Periodically {
    return PaymentType.Periodically(periodCount, period)
}
