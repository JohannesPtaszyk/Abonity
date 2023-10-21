package dev.pott.abonity.core.entity

data class Subscription(
    val id: SubscriptionId,
    val name: String,
    val description: String,
    val paymentInfo: PaymentInfo,
)
