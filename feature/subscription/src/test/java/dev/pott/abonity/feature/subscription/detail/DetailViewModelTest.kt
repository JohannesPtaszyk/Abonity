package dev.pott.abonity.feature.subscription.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.FakeSubscriptionRepository
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.entities.createTestSubscription
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class DetailViewModelTest {

    @Test
    fun `GIVEN savedStateHandle with id argument AND valid subscription with same id WHEN initialize THEN subscription is shown`() {
        runTest {
            val subscription = createTestSubscription()
            val savedStateHandle = SavedStateHandle(
                mapOf("detail_id" to subscription.id.id)
            )
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(subscription)
            )

            val tested = DetailViewModel(
                savedStateHandle,
                subscriptionRepository
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                assertThat(awaitItem()).isEqualTo(DetailState(subscription = subscription))
            }
        }
    }

    @Test
    fun `GIVEN empty savedStateHandle AND valid subscription with same id WHEN setId with existing id THEN subscription is shown`() {
        runTest {
            val subscription = createTestSubscription()
            val savedStateHandle = SavedStateHandle()
            val subscriptionRepository = FakeSubscriptionRepository(
                subscriptionFlow = flowOf(subscription)
            )

            val tested = DetailViewModel(
                savedStateHandle,
                subscriptionRepository
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(DetailState())
                tested.setId(subscription.id)
                assertThat(awaitItem()).isEqualTo(DetailState(subscription = subscription))
            }
        }
    }

}