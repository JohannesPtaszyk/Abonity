package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

private const val WEEK_DAYS = 7

private const val PRICE_UNIT_MULTIPLIER = 100

class PeriodicPriceCalculator @Inject constructor(private val clock: Clock) {
    fun calculateForPeriod(paymentInfo: PaymentInfo, targetPeriod: PaymentPeriod): Price {
        val today = getCurrentLocalDate()
        val price = paymentInfo.price
        return when (val paymentType = paymentInfo.type) {
            PaymentType.OneTime -> {
                if (isFirstPaymentInCurrentPeriod(
                        today,
                        paymentInfo.firstPayment,
                        targetPeriod,
                    )
                ) {
                    paymentInfo.price
                } else {
                    Price.free(paymentInfo.price.currency)
                }
            }

            is PaymentType.Periodic -> {
                calculatePeriodicPrice(
                    today,
                    paymentInfo.firstPayment,
                    targetPeriod,
                    paymentType,
                    price,
                )
            }
        }
    }

    private fun calculatePeriodicPrice(
        today: LocalDate,
        firstPayment: LocalDate,
        targetPeriod: PaymentPeriod,
        type: PaymentType.Periodic,
        price: Price,
    ): Price {
        val paymentsInCurrentPeriod =
            calculatePaymentsInCurrentPeriod(
                today,
                firstPayment,
                targetPeriod,
                type,
            )

        return price * paymentsInCurrentPeriod
    }

    private fun calculatePaymentsInCurrentPeriod(
        today: LocalDate,
        firstPayment: LocalDate,
        targetPeriod: PaymentPeriod,
        type: PaymentType.Periodic,
    ): Int {
        // Calculate the first and last day of the current period
        val firstDayOfCurrentPeriod = getFirstDayOfCurrentPeriod(today, targetPeriod)
        val lastDayOfCurrentPeriod = getLastDayOfCurrentPeriod(today, targetPeriod)

        var possiblePaymentDate = firstPayment

        while (possiblePaymentDate < firstDayOfCurrentPeriod) {
            possiblePaymentDate =
                calculateNextPossiblePaymentDate(possiblePaymentDate, type)
        }

        if (possiblePaymentDate > lastDayOfCurrentPeriod || firstPayment > lastDayOfCurrentPeriod) {
            return 0
        }

        var paymentsInCurrentPeriod = 0

        while (possiblePaymentDate <= lastDayOfCurrentPeriod) {
            paymentsInCurrentPeriod++
            possiblePaymentDate =
                calculateNextPossiblePaymentDate(possiblePaymentDate, type)
        }

        return paymentsInCurrentPeriod
    }

    private fun calculateNextPossiblePaymentDate(
        current: LocalDate,
        type: PaymentType.Periodic,
    ): LocalDate {
        return when (type.period) {
            PaymentPeriod.DAYS -> {
                current + DatePeriod(days = type.periodCount)
            }

            PaymentPeriod.WEEKS -> {
                current + DatePeriod(days = WEEK_DAYS * type.periodCount)
            }

            PaymentPeriod.MONTHS -> {
                current + DatePeriod(months = type.periodCount)
            }

            PaymentPeriod.YEARS -> {
                current + DatePeriod(years = type.periodCount)
            }
        }
    }

    private fun getFirstDayOfCurrentPeriod(
        today: LocalDate,
        targetPeriod: PaymentPeriod,
    ): LocalDate {
        return when (targetPeriod) {
            PaymentPeriod.DAYS -> today
            PaymentPeriod.WEEKS -> {
                today - DatePeriod(days = today.dayOfWeek.ordinal)
            }

            PaymentPeriod.MONTHS -> LocalDate(today.year, today.month, 1)

            PaymentPeriod.YEARS -> LocalDate(today.year, 1, 1)
        }
    }

    private fun getLastDayOfCurrentPeriod(
        today: LocalDate,
        targetPeriod: PaymentPeriod,
    ): LocalDate {
        // Calculate the first day of the current target period
        val firstDayOfCurrentPeriod = getFirstDayOfCurrentPeriod(today, targetPeriod)

        return when (targetPeriod) {
            PaymentPeriod.DAYS -> firstDayOfCurrentPeriod
            PaymentPeriod.WEEKS -> firstDayOfCurrentPeriod + DatePeriod(days = WEEK_DAYS)
            PaymentPeriod.MONTHS -> firstDayOfCurrentPeriod + DatePeriod(months = 1)
            PaymentPeriod.YEARS -> firstDayOfCurrentPeriod + DatePeriod(years = 1)
        } - DatePeriod(days = 1)
    }

    private fun isFirstPaymentInCurrentPeriod(
        today: LocalDate,
        firstPayment: LocalDate,
        period: PaymentPeriod,
    ): Boolean {
        return when (period) {
            PaymentPeriod.DAYS -> firstPayment == today
            PaymentPeriod.WEEKS -> {
                val startOfWeek = today.startOfWeek
                val endOfWeek = today.endOfWeek
                firstPayment in startOfWeek..endOfWeek
            }

            PaymentPeriod.MONTHS -> {
                val isSameYear = firstPayment.year == today.year
                val isSameMonth = firstPayment.month == today.month
                isSameYear && isSameMonth
            }

            PaymentPeriod.YEARS -> firstPayment.year == today.year
        }
    }

    private val LocalDate.startOfWeek: LocalDate
        get() {
            val daysUntilStartOfWeek = dayOfWeek.ordinal
            return this - DatePeriod(days = daysUntilStartOfWeek)
        }

    private val LocalDate.endOfWeek: LocalDate
        get() {
            val daysUntilEndOfWeek =
                DayOfWeek.SUNDAY.ordinal - dayOfWeek.ordinal
            return this + DatePeriod(days = daysUntilEndOfWeek)
        }

    private fun getCurrentLocalDate(): LocalDate {
        val timeZone = TimeZone.currentSystemDefault()
        return clock.now().toLocalDateTime(timeZone).date
    }

    fun calculateTotalForPeriod(
        paymentInfos: List<PaymentInfo>,
        period: PaymentPeriod,
    ): List<Price> {
        return paymentInfos.groupBy {
            it.price.currency
        }.map { currencyListEntry ->
            val currency = currencyListEntry.key
            val value = currencyListEntry.value.sumOf {
                val periodPrice = calculateForPeriod(it, period)
                periodPrice.value * PRICE_UNIT_MULTIPLIER
            } / PRICE_UNIT_MULTIPLIER
            Price(value, currency)
        }.filter {
            it.value > 0
        }
    }
}
