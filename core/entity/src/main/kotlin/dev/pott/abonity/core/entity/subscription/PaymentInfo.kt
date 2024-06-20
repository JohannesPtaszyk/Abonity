package dev.pott.abonity.core.entity.subscription

import kotlinx.datetime.LocalDate

data class PaymentInfo(val price: Price, val firstPayment: LocalDate, val type: PaymentType)
