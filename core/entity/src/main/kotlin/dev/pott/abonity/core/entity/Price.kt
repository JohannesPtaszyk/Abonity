package dev.pott.abonity.core.entity

import java.util.Currency

data class Price(
    val value: Double,
    val currency: Currency,
) {
    operator fun times(multiplier: Int): Price {
        return copy(value = value * multiplier)
    }
    /*
     TODO add methods for calculations
        - calculation functions: add
        - ensure that calculation only works for same currencies
        - calculations should only be done in cents (Use Long values inside calculation)
     */

    companion object {
        fun free(currency: Currency): Price = Price(0.0, currency)
    }
}
