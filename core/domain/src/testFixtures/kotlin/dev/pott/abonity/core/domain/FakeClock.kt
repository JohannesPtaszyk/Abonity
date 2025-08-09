package dev.pott.abonity.core.domain

import kotlinx.datetime.Instant
import kotlin.time.Clock

class FakeClock(private val now: Instant = Instant.parse("2021-03-01T00:00:00Z")) : Clock {
    override fun now(): Instant = now
}
