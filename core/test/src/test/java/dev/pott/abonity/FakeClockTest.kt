package dev.pott.abonity

import assertk.assertThat
import assertk.assertions.isSameAs
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

class FakeClockTest {

    @Test
    fun `now returns same instant as passed`() {
        val instant = Instant.DISTANT_FUTURE
        val clock = FakeClock(instant)

        assertThat(clock.now()).isSameAs(instant)
    }
}
