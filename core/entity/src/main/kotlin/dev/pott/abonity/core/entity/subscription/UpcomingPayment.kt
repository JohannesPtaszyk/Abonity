package dev.pott.abonity.core.entity.subscription

import kotlinx.datetime.LocalDate

data class UpcomingPayment(val subscription: Subscription, val date: LocalDate) {
    val id = "${subscription.id}_$date"
}
