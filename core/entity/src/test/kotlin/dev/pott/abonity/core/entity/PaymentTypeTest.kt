package dev.pott.abonity.core.entity

import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class PaymentTypeTest {
    @Nested
    inner class Periodic {
        @TestFactory
        fun `PaymentType constructor throws for period count below 1`(): List<DynamicTest> {
            return listOf(
                0 to PaymentPeriod.DAYS,
                -1 to PaymentPeriod.WEEKS,
            ).map {
                DynamicTest.dynamicTest(
                    "GIVEN payment period of ${it.first} " +
                        "AND payment period of ${it.second} " +
                        "WHEN invoking constructor THEN throws IllegalArgumentException",
                ) {
                    assertThrows<IllegalStateException> {
                        PaymentType.Periodic(
                            periodCount = it.first,
                            period = it.second,
                        )
                    }
                }
            }
        }
    }
}
