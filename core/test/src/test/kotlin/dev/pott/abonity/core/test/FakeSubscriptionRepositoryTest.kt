package dev.pott.abonity.core.test

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isSameInstanceAs
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeSubscriptionRepositoryTest {

    @Test
    fun `getSubscriptionsFlow returns constructor parameter`() {
        val flow = emptyFlow<List<Subscription>>()

        val tested = FakeSubscriptionRepository(subscriptionsFlow = flow)

        assertThat(tested.getSubscriptionsFlow()).isSameInstanceAs(flow)
    }

    @Test
    fun `getSubscriptionFlow returns flow values from constructor parameter`() =
        runTest {
            val subscription = createTestSubscription()
            val flow = flowOf(subscription)

            val tested = FakeSubscriptionRepository(subscriptionFlow = flow)

            tested.getSubscriptionFlow(subscription.id).test {
                assertThat(awaitItem()).isSameInstanceAs(subscription)
                awaitComplete()
            }
        }

    @Test
    fun `addOrUpdateSubscription returns subscription and adds it to addedSubscriptions`() {
        runTest {
            val subscription = createTestSubscription()

            val tested = FakeSubscriptionRepository()

            assertThat(tested.addOrUpdateSubscription(subscription)).isSameInstanceAs(subscription)
            assertThat(tested.addedSubscriptions.first()).isSameInstanceAs(subscription)
        }
    }
}
