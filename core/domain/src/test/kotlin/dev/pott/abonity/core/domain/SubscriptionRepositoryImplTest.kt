package dev.pott.abonity.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.domain.subscription.SubscriptionRepositoryImpl
import dev.pott.abonity.core.test.subscription.FakeSubscriptionLocalDataSource
import dev.pott.abonity.core.test.subscription.FakeSubscriptionWidgetUpdater
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import dev.pott.abonity.core.test.subscription.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SubscriptionRepositoryImplTest {

    @Test
    fun `GIVEN local returns a subscriptions flow WHEN getSubscriptionsFlow THEN return the same flow`() {
        runTest {
            val localSubscriptions = flowOf(createTestSubscriptionList())
            val fakeLocalDataSource = FakeSubscriptionLocalDataSource(localSubscriptions)
            val fakeSubscriptionWidgetUpdater = FakeSubscriptionWidgetUpdater()

            val tested = SubscriptionRepositoryImpl(
                fakeLocalDataSource,
                fakeSubscriptionWidgetUpdater,
            )
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
            val fakeSubscriptionWidgetUpdater = FakeSubscriptionWidgetUpdater()

            val tested = SubscriptionRepositoryImpl(
                fakeLocalDataSource,
                fakeSubscriptionWidgetUpdater,
            )
            val result = tested.getSubscriptionFlow(subscription.id)
            assertThat(result).isEqualTo(localSubscriptions)
        }
    }

    @Test
    fun `GIVEN subscription ID WHEN deleteSubscription THEN deletes local subscription AND notifies widget update`() {
        runTest {
            val subscription = createTestSubscription()
            val localSubscriptions = flowOf(subscription)
            val fakeLocalDataSource = FakeSubscriptionLocalDataSource(
                testSubscriptionFlow = localSubscriptions,
            )
            val fakeSubscriptionWidgetUpdater = FakeSubscriptionWidgetUpdater()

            val tested = SubscriptionRepositoryImpl(
                fakeLocalDataSource,
                fakeSubscriptionWidgetUpdater,
            )
            tested.deleteSubscription(subscription.id)
            assertThat(fakeLocalDataSource.deletedSubscriptions).isEqualTo(listOf(subscription.id))
            assertThat(fakeSubscriptionWidgetUpdater.updatedCount).isEqualTo(1)
        }
    }
}
