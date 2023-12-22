package dev.pott.abonity.core.test

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.subscription.FakeSubscriptionLocalDataSource
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import dev.pott.abonity.core.test.subscription.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeSubscriptionLocalDataSourceTest {

    @Test
    fun `GIVEN subscriptions flow WHEN getSubscriptionsFlow THEN return same flow `() {
        runTest {
            val subscriptionsFlow = flowOf(createTestSubscriptionList())
            val fake = FakeSubscriptionLocalDataSource(subscriptionsFlow)

            val result = fake.getSubscriptionsFlow()

            assertThat(result).isEqualTo(subscriptionsFlow)
        }
    }

    @Test
    fun `GIVEN subscription flow WHEN getSubscriptionFlow THEN return same flow `() {
        runTest {
            val subscription = createTestSubscription()
            val subscriptionFlow = flowOf(subscription)

            val tested = FakeSubscriptionLocalDataSource(testSubscriptionFlow = subscriptionFlow)

            val result = tested.getSubscriptionFlow(subscription.id)
            assertThat(result).isEqualTo(subscriptionFlow)
        }
    }

    @Test
    fun `GIVEN a subscription WHEN addOrUpdateSubscription THEN add to addedSubscriptions and return it `() {
        runTest {
            val subscription = createTestSubscription()

            val tested = FakeSubscriptionLocalDataSource()

            val result = tested.addOrUpdateSubscription(subscription)
            assertThat(result).isEqualTo(subscription)
            assertThat(tested.addedSubscriptions.first()).isEqualTo(subscription)
        }
    }
}
