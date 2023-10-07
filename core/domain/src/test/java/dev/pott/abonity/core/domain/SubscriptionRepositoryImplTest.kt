package dev.pott.abonity.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.FakeSubscriptionLocalDataSource
import dev.pott.abonity.entities.createTestSubscriptionList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SubscriptionRepositoryImplTest {

    @Test
    fun `GIVEN local returns a subscriptions flow WHEN getSubscriptionFlow THEN return the same flow`() {
        runTest {
            val localSubscriptions = flowOf(createTestSubscriptionList())
            val fakeLocalDataSource = FakeSubscriptionLocalDataSource(
                localSubscriptions
            )
            val tested = SubscriptionRepositoryImpl(fakeLocalDataSource)

            val result = tested.getSubscriptionFlow()

            assertThat(result).isEqualTo(localSubscriptions)
        }
    }
}
