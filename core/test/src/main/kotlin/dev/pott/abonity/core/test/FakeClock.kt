package dev.pott.abonity.core.test

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeClock(private val now: Instant = Instant.parse("2021-03-01T00:00:00Z")) : Clock {
    override fun now(): Instant = now
}
