package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.common.extensions.WEEK_DAYS
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun LocalDate.getFirstDayOfCurrentPeriod(targetPeriod: PaymentPeriod): LocalDate =
    when (targetPeriod) {
        PaymentPeriod.DAYS -> this
        PaymentPeriod.WEEKS -> {
            this - DatePeriod(days = this.dayOfWeek.ordinal)
        }

        PaymentPeriod.MONTHS -> LocalDate(year, month, 1)

        PaymentPeriod.YEARS -> LocalDate(year, 1, 1)
    }

fun LocalDate.getLastDayOfCurrentPeriod(targetPeriod: PaymentPeriod): LocalDate {
    val firstDayOfCurrentPeriod = getFirstDayOfCurrentPeriod(targetPeriod)
    return when (targetPeriod) {
        PaymentPeriod.DAYS -> firstDayOfCurrentPeriod
        PaymentPeriod.WEEKS -> firstDayOfCurrentPeriod + DatePeriod(days = WEEK_DAYS)
        PaymentPeriod.MONTHS -> firstDayOfCurrentPeriod + DatePeriod(months = 1)
        PaymentPeriod.YEARS -> firstDayOfCurrentPeriod + DatePeriod(years = 1)
    } - DatePeriod(days = 1)
}
