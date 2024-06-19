package dev.pott.abonity.core.test.subscription.entities

import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import kotlinx.datetime.LocalDate

fun createTestPaymentInfo(
    price: Price = createTestPrice(),
    firstPayment: LocalDate = LocalDate(2020, 2, 2),
    type: PaymentType = createPeriodicallyPaymentType(),
): PaymentInfo =
    PaymentInfo(
        price,
        firstPayment,
        type,
    )
