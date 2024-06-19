package dev.pott.abonity.core.test.subscription.entities

import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType

fun createPeriodicallyPaymentType(
    periodCount: Int = 1,
    period: PaymentPeriod = PaymentPeriod.MONTHS,
): PaymentType.Periodic = PaymentType.Periodic(periodCount, period)
