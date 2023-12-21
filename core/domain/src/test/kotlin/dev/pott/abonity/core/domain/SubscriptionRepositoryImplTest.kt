package dev.pott.abonity.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.domain.subscription.SubscriptionRepositoryImpl
import dev.pott.abonity.core.test.FakeSubscriptionLocalDataSource
import dev.pott.abonity.core.test.entities.createTestSubscription
import dev.pott.abonity.core.test.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SubscriptionRepositoryImplTest {

    @Test
    fun `GIVEN local returns a subscriptions flow WHEN getSubscriptionsFlow THEN return the same flow`() {
        runTest {
            val localSubscriptions = flowOf(createTestSubscriptionList())
            val fakeLocalDataSource =
                FakeSubscriptionLocalDataSource(
                    localSubscriptions,
                )
            val tested = SubscriptionRepositoryImpl(fakeLocalDataSource)

            val result = tested.getSubscriptionsFlow()

            assertThat(result).isEqualTo(localSubscriptions)
        }
    }

    @Test
    fun `GIVEN local returns a subscription flow WHEN getSubscriptionFlow THEN return the same flow`() {
        runTest {
            val subscription = createTestSubscription()
            val localSubscriptions = flowOf(subscription)
            val fakeLocalDataSource = FakeSubscriptionLocalDataSource(
                testSubscriptionFlow = localSubscriptions,
            )

            val tested = SubscriptionRepositoryImpl(fakeLocalDataSource)

            val result = tested.getSubscriptionFlow(subscription.id)
            assertThat(result).isEqualTo(localSubscriptions)
        }
    }
}
