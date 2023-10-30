package dev.pott.abonity.entities

import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType

fun createPeriodicallyPaymentType(
    periodCount: Int = 1,
    period: PaymentPeriod = PaymentPeriod.MONTHS,
): PaymentType.Periodic {
    return PaymentType.Periodic(periodCount, period)
}
