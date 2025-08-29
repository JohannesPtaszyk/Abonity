package dev.pott.abonity.core.domain

import kotlin.time.Clock
import kotlin.time.Instant

class FakeClock(private val now: Instant = Instant.parse("2021-03-01T00:00:00Z")) : Clock {
    override fun now(): Instant = now
}
