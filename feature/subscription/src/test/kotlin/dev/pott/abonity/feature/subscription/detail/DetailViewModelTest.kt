package dev.pott.abonity.feature.subscription.detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class DetailViewModelTest {

    @Test
    fun `GIVEN empty savedStateHandle AND valid subscription with same id WHEN setId with existing id THEN subscription is shown`() {
        runTest {
            val subscriptions = createTestSubscriptionList(2).toTypedArray()
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(*subscriptions),
            )

            val tested = DetailViewModel(subscriptionRepository, PaymentInfoCalculator(FakeClock()))

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(subscriptions.first().id)
                assertThat(awaitItem().subscription).isEqualTo(subscriptions.first())

                tested.setId(subscriptions[1].id)
                assertThat(awaitItem().subscription).isEqualTo(subscriptions[1])
            }
        }
    }
}
