package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import javax.inject.Inject

class PeriodicPriceCalculator @Inject constructor(private val clock: Clock) {
    fun calculateForPeriod(
        paymentInfo: PaymentInfo,
        period: Period
    ): Price {
        val today = getCurrentLocalDate()
        val (price, firstPayment, type) = paymentInfo
        val isFirstPaymentInNextPeriod = isFirstPaymentInNextPeriod(
            today,
            firstPayment,
            period
        )
        if (isFirstPaymentInNextPeriod) {
            return Price.free(price.currency)
        }
        val isFirstPaymentInCurrentPeriod = isFirstPaymentInCurrentPeriod(
            today,
            firstPayment,
            period,
        )
        return when (type) {
            PaymentType.OneTime -> {
                if (isFirstPaymentInCurrentPeriod) {
                    price
                } else {
                    Price.free(price.currency)
                }
            }

            is PaymentType.Periodic -> {
                calculatePeriodicPrice(
                    today,
                    firstPayment,
                    period,
                    type,
                    price
                )
            }
        }
    }

    private fun calculatePeriodicPrice(
        today: LocalDate,
        firstPayment: LocalDate,
        period: Period,
        type: PaymentType.Periodic,
        price: Price
    ): Price {
        val yearOffset = today.year - firstPayment.year
        val currentPeriodPaymentDate = when (period) {
            Period.MONTH -> {
                val monthOffset =
                    today.month.value - firstPayment.month.value
                firstPayment.plus(
                    DatePeriod(
                        years = yearOffset,
                        months = monthOffset
                    )
                )
            }

            Period.YEAR -> {
                firstPayment.plus(DatePeriod(years = yearOffset))
            }
        }
        val currentPeriodPaymentEndDate = when (period) {
            Period.MONTH -> LocalDate(today.year, today.month, 1).plus(
                DatePeriod(months = 1)
            )

            Period.YEAR -> LocalDate(
                today.year,
                1,
                1
            ).plus(DatePeriod(years = 1))
        }
        val dateTimeUnit = when (type.period) {
            PaymentPeriod.DAYS -> DateTimeUnit.DAY
            PaymentPeriod.WEEKS -> DateTimeUnit.WEEK
            PaymentPeriod.MONTHS -> DateTimeUnit.MONTH
            PaymentPeriod.YEARS -> DateTimeUnit.YEAR
        }
        val occurrencesInPeriod = currentPeriodPaymentDate
            .until(currentPeriodPaymentEndDate, dateTimeUnit)
            .coerceAtLeast(1)
        return price * (occurrencesInPeriod / type.periodCount)
    }

    private fun isFirstPaymentInNextPeriod(
        currentDate: LocalDate,
        firstPayment: LocalDate,
        period: Period,
    ): Boolean {
        return when (period) {
            Period.MONTH -> firstPayment.month > currentDate.month
            Period.YEAR -> firstPayment.year > currentDate.year
        }
    }

    private fun isFirstPaymentInCurrentPeriod(
        currentDate: LocalDate,
        firstPayment: LocalDate,
        period: Period,
    ): Boolean {
        return when (period) {
            Period.MONTH -> firstPayment.month == currentDate.month
            Period.YEAR -> firstPayment.year == currentDate.year
        }
    }

    private fun getCurrentLocalDate(): LocalDate {
        val timeZone = TimeZone.currentSystemDefault()
        return clock.now().toLocalDateTime(timeZone).date
    }

    enum class Period {
        MONTH, YEAR
    }
}
