package dev.pott.abonity.core.local.subscription.db.entities

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class SubscriptionEntityTest {

    @TestFactory
    fun `Test initialization throws for invalid periodic data`(): List<DynamicTest> {
        return listOf(
            PeriodicTestCase(period = null, periodCount = 1),
            PeriodicTestCase(period = LocalPaymentPeriod.MONTHS, periodCount = null),
        ).map {
            DynamicTest.dynamicTest("GIVEN $it WHEN init THEN throw excpetion") {
                assertThrows<IllegalStateException> {
                    SubscriptionEntity(
                        id = 1L,
                        name = "Name",
                        description = "Description",
                        price = 9.99,
                        currency = "EUR",
                        firstPaymentLocalDate = "2023-03-03",
                        paymentType = LocalPaymentType.PERIODICALLY,
                        periodCount = it.periodCount,
                        period = it.period,
                    )
                }
            }
        }
    }

    data class PeriodicTestCase(
        val period: LocalPaymentPeriod?,
        val periodCount: Int?,
    )
}
