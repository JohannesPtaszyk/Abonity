package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.common.extensions.WEEK_DAYS
import dev.pott.abonity.common.extensions.endOfWeek
import dev.pott.abonity.common.extensions.startOfWeek
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject

private const val PRICE_UNIT_MULTIPLIER = 100

class PaymentInfoCalculator @Inject constructor(private val clock: Clock) {
    fun getTotalPriceForPeriod(paymentInfo: PaymentInfo, targetPeriod: PaymentPeriod): Price {
        val price = paymentInfo.price
        return when (val paymentType = paymentInfo.type) {
            PaymentType.OneTime -> {
                if (isFirstPaymentInCurrentPeriod(paymentInfo.firstPayment, targetPeriod)) {
                    paymentInfo.price
                } else {
                    Price.free(paymentInfo.price.currency)
                }
            }

            is PaymentType.Periodic -> {
                calculatePeriodicPrice(
                    paymentInfo.firstPayment,
                    targetPeriod,
                    paymentType,
                    price,
                )
            }
        }
    }

    fun getTotalPricesForPeriod(
        paymentInfos: List<PaymentInfo>,
        period: PaymentPeriod,
    ): List<Price> =
        paymentInfos.groupBy {
            it.price.currency
        }.map { currencyListEntry ->
            val currency = currencyListEntry.key
            val value = currencyListEntry.value.sumOf {
                val periodPrice = getTotalPriceForPeriod(it, period)
                periodPrice.value * PRICE_UNIT_MULTIPLIER
            } / PRICE_UNIT_MULTIPLIER
            Price(value, currency)
        }.filter {
            it.value > 0
        }

    fun getPaymentDatesForCurrentPeriod(
        firstPayment: LocalDate,
        targetPeriod: PaymentPeriod,
        type: PaymentType,
    ): List<LocalDate> {
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        val lastDayOfCurrentPeriod = today.getLastDayOfCurrentPeriod(targetPeriod)
        val firstDayOfCurrentPeriod = today.getFirstDayOfCurrentPeriod(targetPeriod)
        return when (type) {
            is PaymentType.OneTime -> {
                buildList {
                    firstPayment.takeIf {
                        it in firstDayOfCurrentPeriod..lastDayOfCurrentPeriod
                    }?.let {
                        add(it)
                    }
                }
            }

            is PaymentType.Periodic -> {
                val possiblePaymentDate = findFirstPaymentOfPeriod(
                    firstPayment,
                    firstDayOfCurrentPeriod,
                    type,
                )
                if (
                    possiblePaymentDate > lastDayOfCurrentPeriod ||
                    firstPayment > lastDayOfCurrentPeriod
                ) {
                    return emptyList()
                }

                getPeriodicPaymentDatesForPeriod(
                    firstPayment,
                    possiblePaymentDate,
                    lastDayOfCurrentPeriod,
                    type,
                )
            }
        }
    }

    fun getNextDateByType(
        type: PaymentType.Periodic,
        from: LocalDate = clock.todayIn(TimeZone.currentSystemDefault()),
    ): LocalDate =
        when (type.period) {
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

    fun findFirstPaymentOfPeriod(
        firstPayment: LocalDate,
        beginOfPeriod: LocalDate,
        periodicType: PaymentType.Periodic,
    ): LocalDate {
        var possiblePaymentDate = firstPayment
        val endOfPeriod =
            beginOfPeriod.getLastDayOfCurrentPeriod(periodicType.period) + DatePeriod(days = 1)
        while (possiblePaymentDate < beginOfPeriod && possiblePaymentDate < endOfPeriod) {
            possiblePaymentDate = getNextDateByType(
                periodicType,
                possiblePaymentDate,
            )
        }
        return possiblePaymentDate
    }

    private fun getPeriodicPaymentDatesForPeriod(
        firstPayment: LocalDate,
        beginOfPeriod: LocalDate,
        endOfPeriod: LocalDate,
        periodicType: PaymentType.Periodic,
    ): List<LocalDate> {
        var potentialLastPaymentDate = findFirstPaymentOfPeriod(
            firstPayment,
            beginOfPeriod,
            periodicType,
        )

        var paymentsInCurrentPeriod = emptyList<LocalDate>()
        while (potentialLastPaymentDate <= endOfPeriod) {
            paymentsInCurrentPeriod = paymentsInCurrentPeriod + potentialLastPaymentDate
            potentialLastPaymentDate = getNextDateByType(
                periodicType,
                potentialLastPaymentDate,
            )
        }

        return paymentsInCurrentPeriod
    }

    private fun calculatePeriodicPrice(
        firstPayment: LocalDate,
        targetPeriod: PaymentPeriod,
        type: PaymentType.Periodic,
        price: Price,
    ): Price {
        val paymentsInCurrentPeriod = getPaymentDatesForCurrentPeriod(
            firstPayment,
            targetPeriod,
            type,
        )

        return price * paymentsInCurrentPeriod.size
    }

    private fun isFirstPaymentInCurrentPeriod(
        firstPayment: LocalDate,
        period: PaymentPeriod,
    ): Boolean {
        val today = clock.todayIn(TimeZone.currentSystemDefault())
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
}
