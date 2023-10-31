package dev.pott.abonity.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.FakeSubscriptionLocalDataSource
import dev.pott.abonity.core.test.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SubscriptionRepositoryImplTest {
    @Test
    fun `GIVEN local returns a subscriptions flow WHEN getSubscriptionFlow THEN return the same flow`() {
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
}
