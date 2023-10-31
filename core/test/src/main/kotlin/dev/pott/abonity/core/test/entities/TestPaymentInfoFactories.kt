package dev.pott.abonity.core.test.entities

import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import kotlinx.datetime.LocalDate

fun createTestPaymentInfo(
    price: Price = createTestPrice(),
    firstPayment: LocalDate = LocalDate(2020, 2, 2),
    type: PaymentType = createPeriodicallyPaymentType(),
): PaymentInfo {
    return PaymentInfo(
        price,
        firstPayment,
        type,
    )
}
