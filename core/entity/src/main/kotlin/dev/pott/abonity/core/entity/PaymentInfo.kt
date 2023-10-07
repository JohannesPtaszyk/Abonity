package dev.pott.abonity.core.entity

import kotlinx.datetime.LocalDate

data class PaymentInfo(
    val price: Price,
    val firstPayment: LocalDate,
    val type: PaymentType,
)
