package dev.pott.abonity.core.test.entities

import dev.pott.abonity.core.entity.Price
import java.util.Currency

fun createTestPrice(
    value: Double = 9.99,
    currency: Currency = Currency.getInstance("EUR"),
): Price {
    return Price(value, currency)
}
