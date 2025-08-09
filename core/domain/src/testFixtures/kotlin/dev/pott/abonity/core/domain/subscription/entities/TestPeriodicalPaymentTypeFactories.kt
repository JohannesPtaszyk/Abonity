package dev.pott.abonity.core.domain.subscription.entities

import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType

fun createPeriodicallyPaymentType(
    periodCount: Int = 1,
    period: PaymentPeriod = PaymentPeriod.MONTHS,
): PaymentType.Periodic = PaymentType.Periodic(periodCount, period)
