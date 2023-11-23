package dev.pott.abonity.common.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

const val WEEK_DAYS = 7

val LocalDate.startOfWeek: LocalDate
    get() {
        val daysUntilStartOfWeek = dayOfWeek.ordinal
        return this - DatePeriod(days = daysUntilStartOfWeek)
    }

val LocalDate.endOfWeek: LocalDate
    get() {
        val daysUntilEndOfWeek =
            DayOfWeek.SUNDAY.ordinal - dayOfWeek.ordinal
        return this + DatePeriod(days = daysUntilEndOfWeek)
    }

fun Clock.getCurrentDate() = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
