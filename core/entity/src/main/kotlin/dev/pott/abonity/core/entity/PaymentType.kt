package dev.pott.abonity.core.entity

sealed interface PaymentType {

    data object OneTime : PaymentType

    data class Periodic(
        val periodCount: Int,
        val period: PaymentPeriod,
    ) : PaymentType {
        init {
            check(periodCount in 1..Int.MAX_VALUE) {
                "Period count must be min of 1 and max of Int.MAX_VALUE"
            }
        }
    }
}
