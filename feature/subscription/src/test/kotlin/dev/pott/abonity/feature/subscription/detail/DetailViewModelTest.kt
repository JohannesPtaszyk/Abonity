package dev.pott.abonity.feature.subscription.detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.FakeClock
import dev.pott.abonity.core.domain.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.entities.createTestSubscription
import dev.pott.abonity.core.domain.subscription.entities.createTestSubscriptionList
import dev.pott.abonity.core.entity.subscription.NotificationConfig
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.time.Instant

@ExtendWith(CoroutinesTestExtension::class)
class DetailViewModelTest {

    @Test
    fun `GIVEN valid subscription WHEN setId with existing id THEN subscription is shown AND next payment date is added`() {
        runTest {
            val subscriptions = createTestSubscriptionList(2).toTypedArray()
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(*subscriptions),
            )

            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(subscriptions.first().id)
                assertThat(awaitItem()).isEqualTo(
                    DetailState(
                        subscription = subscriptions.first(),
                        nextPayment = Instant.parse("2021-04-01T00:00:00Z")
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    ),
                )

                tested.setId(subscriptions[1].id)
                assertThat(awaitItem()).isEqualTo(
                    DetailState(
                        subscription = subscriptions[1],
                        nextPayment = Instant.parse("2021-04-01T00:00:00Z")
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN valid subscription WHEN setId with invalid id THEN subscription is not shown`() {
        runTest {
            val subscriptions = createTestSubscriptionList(2).toTypedArray()
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(*subscriptions),
            )

            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(SubscriptionId(100))
                assertThat(cancelAndConsumeRemainingEvents()).isEmpty()
            }
        }
    }

    @Test
    fun `GIVEN loaded subscription WHEN setNotificationConfig with config THEN subscription is saved with config`() {
        runTest {
            val subscription = createTestSubscription()
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(subscription),
            )
            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(subscription.id)
                awaitItem() // consume the loaded state

                val config = NotificationConfig(daysBeforePayment = 3)
                tested.setNotificationConfig(config)

                assertThat(subscriptionRepository.addedSubscriptions.last())
                    .isEqualTo(subscription.copy(notificationConfig = config))
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN loaded subscription with config WHEN setNotificationConfig with null THEN subscription is saved with no config`() {
        runTest {
            val subscription = createTestSubscription(
                notificationConfig = NotificationConfig(daysBeforePayment = 1),
            )
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(subscription),
            )
            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(subscription.id)
                awaitItem() // consume the loaded state

                tested.setNotificationConfig(null)

                assertThat(subscriptionRepository.addedSubscriptions.last().notificationConfig)
                    .isNull()
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN no subscription loaded WHEN setNotificationConfig THEN nothing is saved`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.setNotificationConfig(NotificationConfig(daysBeforePayment = 0))

            assertThat(subscriptionRepository.addedSubscriptions).isEmpty()
        }
    }
}
