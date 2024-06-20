package dev.pott.abonity.core.test.subscription.entities

import dev.pott.abonity.core.entity.subscription.Price
import java.util.Currency

fun createTestPrice(
    value: Double = 9.99,
    currency: Currency = Currency.getInstance("EUR"),
): Price = Price(value, currency)
