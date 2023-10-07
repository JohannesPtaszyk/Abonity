package dev.pott.abonity.core.entity

import java.util.Currency

data class Price(
    val value: Double,
    val currency: Currency
) {
    /*
     TODO add methods for calculations
        - calculation functions: add
        - ensure that calculation only works for same currencies
        - calculations should only be done in cents (Use Long values inside calculation)
     */
}
