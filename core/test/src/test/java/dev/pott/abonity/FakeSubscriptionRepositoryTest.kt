package dev.pott.abonity

import assertk.assertThat
import assertk.assertions.isSameAs
import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Test

class FakeSubscriptionRepositoryTest {

    @Test
    fun `getSubscriptionFlow returns constructor parameter`() {
        val flow = emptyFlow<List<Subscription>>()
        val tested = FakeSubscriptionRepository(flow)
        assertThat(tested.getSubscriptionsFlow()).isSameAs(flow)
    }
}
