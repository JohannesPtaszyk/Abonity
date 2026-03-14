package dev.pott.abonity.core.local.subscription

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.pott.abonity.core.domain.subscription.entities.createTestPaymentInfo
import dev.pott.abonity.core.domain.subscription.entities.createTestSubscription
import dev.pott.abonity.core.entity.subscription.NotificationConfig
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.local.fakes.FakeSubscriptionDao
import dev.pott.abonity.core.local.testdata.createTestSubscriptionCategoryEntityWithOneTimePayment
import dev.pott.abonity.core.local.testdata.createTestSubscriptionCategoryEntityWithPeriodicPayment
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RoomSubscriptionDataSourceTest {
    @Test
    fun `GIVEN flow of subscription entities WHEN getSubscriptionFlow THEN return flow of domain subscriptions`() {
        runTest {
            val entities = listOf(
                createTestSubscriptionCategoryEntityWithPeriodicPayment(id = 1),
                createTestSubscriptionCategoryEntityWithOneTimePayment(id = 2),
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

    @Test
    fun `GIVEN entity with notificationDaysBefore set WHEN getSubscriptionsFlow THEN domain has matching NotificationConfig`() {
        runTest {
            val entity = createTestSubscriptionCategoryEntityWithPeriodicPayment(
                id = 1,
                notificationDaysBefore = 3,
            )
            val dao = FakeSubscriptionDao(initialEntities = listOf(entity))
            val dataSource = RoomSubscriptionDataSource(dao)

            dataSource.getSubscriptionsFlow().test {
                val subscription = awaitItem().first()
                assertThat(subscription.notificationConfig)
                    .isEqualTo(NotificationConfig(daysBeforePayment = 3))
            }
        }
    }

    @Test
    fun `GIVEN entity with null notificationDaysBefore WHEN getSubscriptionsFlow THEN domain has null notificationConfig`() {
        runTest {
            val entity = createTestSubscriptionCategoryEntityWithPeriodicPayment(
                id = 1,
                notificationDaysBefore = null,
            )
            val dao = FakeSubscriptionDao(initialEntities = listOf(entity))
            val dataSource = RoomSubscriptionDataSource(dao)

            dataSource.getSubscriptionsFlow().test {
                val subscription = awaitItem().first()
                assertThat(subscription.notificationConfig).isNull()
            }
        }
    }

    @Test
    fun `GIVEN subscription with NotificationConfig WHEN addOrUpdateSubscription THEN entity has correct notificationDaysBefore`() {
        runTest {
            val dao = FakeSubscriptionDao()
            val dataSource = RoomSubscriptionDataSource(dao)
            val subscription = createTestSubscription(
                notificationConfig = NotificationConfig(daysBeforePayment = 7),
            )

            dataSource.addOrUpdateSubscription(subscription)

            dao.getSubscriptionsFlow().test {
                assertThat(awaitItem().first().subscription.notificationDaysBefore).isEqualTo(7)
            }
        }
    }

    @Test
    fun `GIVEN subscription with null notificationConfig WHEN addOrUpdateSubscription THEN entity has null notificationDaysBefore`() {
        runTest {
            val dao = FakeSubscriptionDao()
            val dataSource = RoomSubscriptionDataSource(dao)
            val subscription = createTestSubscription(notificationConfig = null)

            dataSource.addOrUpdateSubscription(subscription)

            dao.getSubscriptionsFlow().test {
                assertThat(awaitItem().first().subscription.notificationDaysBefore).isNull()
            }
        }
    }
}
