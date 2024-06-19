package dev.pott.abonity.core.local.subscription.mapper

import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.datetime.LocalDate
import java.util.Currency

fun Subscription.toEntity(): SubscriptionCategoryEntity {
    val paymentType = paymentInfo.type
    val (period, periodCount) = if (paymentType is PaymentType.Periodic) {
        paymentType.period.toEntity() to paymentType.periodCount
    } else {
        null to null
    }
    return SubscriptionCategoryEntity(
        subscription = SubscriptionEntity(
            id.value,
            name,
            description,
            paymentInfo.price.value,
            paymentInfo.price.currency.currencyCode,
            paymentInfo.firstPayment.toString(),
            paymentType.toEntity(),
            periodCount,
            period,
        ),
        categories = categories.map { it.toEntity() },
    )
}

fun SubscriptionCategoryEntity.toDomain(): Subscription = subscription.toDomain(categories)

private fun SubscriptionEntity.toDomain(categories: List<CategoryEntity>): Subscription {
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
        categories.map { it.toDomain() },
    )
}
