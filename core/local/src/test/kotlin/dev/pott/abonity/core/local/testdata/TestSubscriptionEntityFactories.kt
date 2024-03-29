package dev.pott.abonity.core.local.testdata

import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentPeriod
import dev.pott.abonity.core.local.subscription.db.entities.LocalPaymentType
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity

fun createTestSubscriptionCategoryEntityWithPeriodicPayment(
    id: Long = 1337,
    name: String = "Name Periodic",
    description: String = "Description Periodic",
    price: Double = 9.99,
    currency: String = "EUR",
    firstPaymentLocalDate: String = "2020-02-02",
    periodCount: Int = 1,
    period: LocalPaymentPeriod = LocalPaymentPeriod.MONTHS,
    categories: List<CategoryEntity> = listOf(CategoryEntity(1, "Test Category")),
) = SubscriptionCategoryEntity(
    subscription = createSubscriptionEntityWithPeriodicPayment(
        id = id,
        name = name,
        description = description,
        price = price,
        currency = currency,
        firstPaymentLocalDate = firstPaymentLocalDate,
        periodCount = periodCount,
        period = period,
    ),
    categories = categories,
)

fun createTestSubscriptionCategoryEntityWithOneTimePayment(
    id: Long = 1337,
    name: String = "Name One Time",
    description: String = "Description One Time",
    price: Double = 9.99,
    currency: String = "EUR",
    firstPaymentLocalDate: String = "2020-02-02",
    categories: List<CategoryEntity> = listOf(CategoryEntity(1, "Test Category")),
) = SubscriptionCategoryEntity(
    subscription = createSubscriptionEntityWithOneTimePayment(
        id = id,
        name = name,
        description = description,
        price = price,
        currency = currency,
        firstPaymentLocalDate = firstPaymentLocalDate,
    ),
    categories = categories,
)

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
