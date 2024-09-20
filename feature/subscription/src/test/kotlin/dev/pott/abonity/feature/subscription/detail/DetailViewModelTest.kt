package dev.pott.abonity.feature.subscription.detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

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
}
