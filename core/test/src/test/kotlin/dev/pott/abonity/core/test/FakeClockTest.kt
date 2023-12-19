package dev.pott.abonity.core.test

import assertk.assertThat
import assertk.assertions.isSameInstanceAs
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

class FakeClockTest {
    @Test
    fun `now returns same instant as passed`() {
        val instant = Instant.DISTANT_FUTURE
        val clock = FakeClock(instant)

        assertThat(clock.now()).isSameInstanceAs(instant)
    }
}
