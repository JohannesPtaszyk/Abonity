package dev.pott.abonity

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeSubscriptionLocalDataSourceTest {

    @Test
    fun `GIVEN subscriptions WHEN getSubscriptionFlow THEN return it as flow `() {
        runTest {
            val subscriptionsFlow = flowOf(createTestSubscriptionList())
            val fake = FakeSubscriptionLocalDataSource(subscriptionsFlow)

            val result = fake.getSubscriptionsFlow()

            assertThat(result).isEqualTo(subscriptionsFlow)
        }
    }
}
