package dev.pott.abonity.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.domain.subscription.PaymentDateCalculator
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.test.FakeClock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.Currency

class PaymentInfoCalculatorTest {
    @TestFactory
    fun testCalculateMonthlyPriceOneTime(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // One time payment this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // One time payment next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceMonthlyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Monthly payment starting last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // Monthly payment starting this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // Monthly payment starting next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceBiMonthlyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Bi-Monthly payment starting last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Bi-Monthly payment starting this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // Monthly payment starting next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Monthly payment starting two month ago
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 3, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceYearlyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Yearly payment last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Yearly payment this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // Yearly payment next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceBiYearlyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Bi-Yearly payment last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Bi-Yearly payment this month and year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // Bi-Yearly payment next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Bi-Yearly payment this month but 2nd year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2020, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.YEARS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceDailyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Daily payment starting last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price(28.0, testCurrency),
            ),
            // Daily payment starting beginning of this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(31.0, testCurrency),
            ),
            // Daily payment starting middle of this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 16),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(16.0, testCurrency),
            ),
            // Daily payment starting next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateMonthlyPriceBiDailyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Bi-Daily payment starting last month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 3, 1),
                expectedPrice = Price(15.0, testCurrency),
            ),
            // Bi-Daily payment starting beginning of this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(16.0, testCurrency),
            ),
            // Bi-Daily payment starting middle of this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 16),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(8.0, testCurrency),
            ),
            // Bi-Daily payment starting next month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN payment info = ${testCase.paymentInfo}
                AND today = ${testCase.today}
                EXPECT monthly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.MONTHS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateYearlyPriceOneTime(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // One time payment this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // One time payment next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN ${testCase.paymentInfo}
                AND ${testCase.today}
                EXPECT yearly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.YEARS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateYearlyPriceMonthlyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // One time payment this month
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(1.0, testCurrency),
            ),
            // One time payment next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.OneTime,
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
            // Monthly payment starting first month of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price(12.0, testCurrency),
            ),
            // Monthly payment starting middle of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 7, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(6.0, testCurrency),
            ),
            // Monthly payment starting last year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2020, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(12.0, testCurrency),
            ),
            // Monthly payment starting next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN ${testCase.paymentInfo}
                AND ${testCase.today}
                EXPECT yearly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.YEARS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateYearlyPriceBiMonthlyPayment(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Bi-Monthly payment starting first month of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price(6.0, testCurrency),
            ),
            // Bi-Monthly payment starting middle of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 7, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(3.0, testCurrency),
            ),
            // Bi-Monthly payment starting last year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2020, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(6.0, testCurrency),
            ),
            // Bi-Monthly payment starting next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN ${testCase.paymentInfo}
                AND ${testCase.today}
                EXPECT yearly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.YEARS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateYearlyPriceDailyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Daily payment starting first day of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 2, 1),
                expectedPrice = Price(365.0, testCurrency),
            ),
            // Daily payment starting middle of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 7, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(184.0, testCurrency),
            ),
            // Daily payment starting last year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2020, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(365.0, testCurrency),
            ),
            // Daily payment starting next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN ${testCase.paymentInfo}
                AND ${testCase.today}
                EXPECT yearly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.YEARS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    @TestFactory
    fun testCalculateYearlyPriceBiDailyBase(): List<DynamicTest> {
        val testCurrency = Currency.getInstance("EUR")
        val testCases = listOf(
            // Bi-Daily payment starting first month of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(183.0, testCurrency),
            ),
            // Bi-Daily payment starting middle of year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2021, 7, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(92.0, testCurrency),
            ),
            // Bi-Daily payment starting last year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2020, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price(183.0, testCurrency),
            ),
            // Bi-Daily payment starting next year
            TestCase(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, testCurrency),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
                today = LocalDate(2021, 1, 1),
                expectedPrice = Price.free(testCurrency),
            ),
        )
        return testCases.mapIndexed { i: Int, testCase: TestCase ->
            DynamicTest.dynamicTest(
                """
                ($i) GIVEN ${testCase.paymentInfo}
                AND ${testCase.today}
                EXPECT yearly price = ${testCase.expectedPrice}
                """.trimIndent(),
            ) {
                val todayInstant = Instant.parse(testCase.today.toString() + "T00:00:00Z")
                val clock = FakeClock(todayInstant)
                val dateCalculator = PaymentDateCalculator(clock)

                val result = PaymentInfoCalculator(dateCalculator, clock).getTotalPriceForPeriod(
                    testCase.paymentInfo,
                    PaymentPeriod.YEARS,
                )

                assertThat(result).isEqualTo(testCase.expectedPrice)
            }
        }
    }

    private data class TestCase(
        val paymentInfo: PaymentInfo,
        val today: LocalDate,
        val expectedPrice: Price,
    )
}
