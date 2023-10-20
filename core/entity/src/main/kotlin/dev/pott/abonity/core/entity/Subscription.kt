package dev.pott.abonity.core.entity

data class Subscription(
    val id: Long,
    val name: String,
    val description: String,
    val paymentInfo: PaymentInfo,
)
