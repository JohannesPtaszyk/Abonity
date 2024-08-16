package dev.pott.abonity.core.entity.subscription

import kotlinx.datetime.LocalDate

data class UpcomingPayments(
    val payments: Map<LocalDate, List<UpcomingPayment>>,
    val hasSubscriptions: Boolean,
    val period: PaymentPeriod,
)
