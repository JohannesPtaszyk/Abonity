package dev.pott.abonity.core.entity.subscription

import java.util.Currency

data class Price(val value: Double, val currency: Currency) {
    operator fun times(multiplier: Int): Price = copy(value = value * multiplier)

    companion object {
        fun free(currency: Currency): Price = Price(0.0, currency)
    }
}
