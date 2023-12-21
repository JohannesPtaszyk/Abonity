package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.common.extensions.WEEK_DAYS
import dev.pott.abonity.common.extensions.getCurrentDate
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import javax.inject.Inject

class PaymentDateCalculator @Inject constructor(
    private val clock: Clock,
) {

    fun calculateNextPossiblePaymentDate(
        type: PaymentType.Periodic,
        from: LocalDate = clock.getCurrentDate(),
    ): LocalDate {
        return when (type.period) {
            PaymentPeriod.DAYS -> {
                from + DatePeriod(days = type.periodCount)
            }

            PaymentPeriod.WEEKS -> {
                from + DatePeriod(days = WEEK_DAYS * type.periodCount)
            }

            PaymentPeriod.MONTHS -> {
                from + DatePeriod(months = type.periodCount)
            }

            PaymentPeriod.YEARS -> {
                from + DatePeriod(years = type.periodCount)
            }
        }
    }

    fun firstPaymentAfterBeginOfPeriod(
        firstPayment: LocalDate,
        beginOfPeriod: LocalDate,
        periodicType: PaymentType.Periodic,
    ): LocalDate {
        var possiblePaymentDate = firstPayment
        while (possiblePaymentDate < beginOfPeriod) {
            possiblePaymentDate = calculateNextPossiblePaymentDate(
                periodicType,
                possiblePaymentDate,
            )
        }
        return possiblePaymentDate
    }
}
