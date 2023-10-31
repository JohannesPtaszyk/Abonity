package dev.pott.abonity.core.local.testdata

import dev.pott.abonity.core.local.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.db.entities.LocalPaymentType
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity

fun createSubscriptionEntityWithPeriodicPayment(
    id: Long = 1337,
    name: String = "Name Periodic",
    description: String = "Description Periodic",
    price: Double = 9.99,
    currency: String = "EUR",
    firstPaymentLocalDate: String = "2020-02-02",
    periodCount: Int = 1,
    period: LocalPaymentPeriod = LocalPaymentPeriod.MONTHS,
) = SubscriptionEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    currency = currency,
    firstPaymentLocalDate = firstPaymentLocalDate,
    paymentType = LocalPaymentType.PERIODICALLY,
    periodCount = periodCount,
    period = period,
)

fun createSubscriptionEntityWithOneTimePayment(
    id: Long = 1337,
    name: String = "Name One Time",
    description: String = "Description One Time",
    price: Double = 9.99,
    currency: String = "EUR",
    firstPaymentLocalDate: String = "2020-02-02",
) = SubscriptionEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    currency = currency,
    firstPaymentLocalDate = firstPaymentLocalDate,
    paymentType = LocalPaymentType.ONE_TIME,
    periodCount = null,
    period = null,
)
