package dev.pott.abonity.core.entity.subscription

data class Subscription(
    val id: SubscriptionId = SubscriptionId.none(),
    val name: String,
    val description: String,
    val paymentInfo: PaymentInfo,
)
