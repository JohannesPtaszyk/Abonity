package dev.pott.abonity.core.local.subscription

import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod

fun LocalPaymentPeriod.toDomain() =
    when (this) {
        LocalPaymentPeriod.DAYS -> PaymentPeriod.DAYS
        LocalPaymentPeriod.WEEKS -> PaymentPeriod.WEEKS
        LocalPaymentPeriod.MONTHS -> PaymentPeriod.MONTHS
        LocalPaymentPeriod.YEARS -> PaymentPeriod.YEARS
    }

fun PaymentPeriod.toEntity(): LocalPaymentPeriod =
    when (this) {
        PaymentPeriod.DAYS -> LocalPaymentPeriod.DAYS
        PaymentPeriod.WEEKS -> LocalPaymentPeriod.WEEKS
        PaymentPeriod.MONTHS -> LocalPaymentPeriod.MONTHS
        PaymentPeriod.YEARS -> LocalPaymentPeriod.YEARS
    }
