package dev.pott.abonity.core.local

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.local.fakes.FakeSubscriptionDao
import dev.pott.abonity.core.local.subscription.RoomSubscriptionDataSource
import dev.pott.abonity.core.local.testdata.createSubscriptionEntityWithOneTimePayment
import dev.pott.abonity.core.local.testdata.createSubscriptionEntityWithPeriodicPayment
import dev.pott.abonity.core.test.subscription.entities.createTestPaymentInfo
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RoomSubscriptionDataSourceTest {
    @Test
    fun `GIVEN flow of subscription entities WHEN getSubscriptionFlow THEN return flow of domain subscriptions`() {
        runTest {
            val entities = listOf(
                createSubscriptionEntityWithPeriodicPayment(id = 1),
                createSubscriptionEntityWithOneTimePayment(id = 2),
            )
            val dao = FakeSubscriptionDao(initialEntities = entities)
            val dataSource = RoomSubscriptionDataSource(dao)

            val expected = listOf(
                createTestSubscription(
                    id = 1,
                    name = "Name Periodic",
                    description = "Description Periodic",
                    paymentInfo =
                    createTestPaymentInfo(
                        type =
                        PaymentType.Periodic(
                            periodCount = 1,
                            period = PaymentPeriod.MONTHS,
                        ),
                    ),
                ),
                createTestSubscription(
                    id = 2,
                    name = "Name One Time",
                    description = "Description One Time",
                    paymentInfo =
                    createTestPaymentInfo(
                        type = PaymentType.OneTime,
                    ),
                ),
            )
            dataSource.getSubscriptionsFlow().test {
                assertThat(awaitItem()).isEqualTo(expected)
            }
        }
    }
}
