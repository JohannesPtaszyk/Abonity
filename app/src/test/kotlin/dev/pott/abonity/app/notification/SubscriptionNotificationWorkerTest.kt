package dev.pott.abonity.app.notification

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.pott.abonity.core.domain.FakeClock
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.entities.createTestPaymentInfo
import dev.pott.abonity.core.domain.subscription.entities.createTestSubscription
import dev.pott.abonity.core.entity.subscription.NotificationConfig
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.junit.jupiter.api.Test

class SubscriptionNotificationWorkerTest {

    private val clock = FakeClock()
    private val calculator = PaymentInfoCalculator(clock)
    private val today = clock.todayIn(TimeZone.currentSystemDefault())
    private val todayEpochDays = today.toEpochDays()

    @Test
    fun `GIVEN subscription without notificationConfig WHEN daysUntilNextPayment THEN returns null`() {
        val subscription = createTestSubscription(notificationConfig = null)

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isNull()
    }

    @Test
    fun `GIVEN one-time subscription with payment today WHEN daysUntilNextPayment THEN returns 0`() {
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = today,
                type = PaymentType.OneTime,
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = 0),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `GIVEN one-time subscription with payment in 7 days WHEN daysUntilNextPayment THEN returns 7`() {
        val paymentDate = today.plus(DatePeriod(days = 7))
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = paymentDate,
                type = PaymentType.OneTime,
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = 7),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(7)
    }

    @Test
    fun `GIVEN one-time subscription where payment days don't match config WHEN daysUntilNextPayment THEN result differs from daysBeforePayment`() {
        val paymentDate = today.plus(DatePeriod(days = 5))
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = paymentDate,
                type = PaymentType.OneTime,
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = 3),
        )

        val days = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(days).isEqualTo(5)
        assertThat(days == subscription.notificationConfig!!.daysBeforePayment).isEqualTo(false)
    }

    @Test
    fun `GIVEN periodic subscription with firstPayment today WHEN daysUntilNextPayment THEN returns 0`() {
        // firstPayment == today, so the next upcoming occurrence is today itself
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = today,
                type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = 0),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `GIVEN periodic subscription with firstPayment 5 days before today WHEN daysUntilNextPayment THEN returns days to next occurrence on subscription payment day`() {
        // today = 2021-03-01, firstPayment = 2021-02-24 (5 days ago)
        // iteration: Feb 24 < Mar 1 → advance to Mar 24
        // next payment = Mar 24 = 23 days from today
        val firstPayment = today.minus(DatePeriod(days = 5))
        val expectedNextPayment = firstPayment.plus(DatePeriod(months = 1))
        val expectedDays = (expectedNextPayment.toEpochDays() - todayEpochDays).toInt()
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = firstPayment,
                type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = expectedDays),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(expectedDays)
    }

    @Test
    fun `GIVEN periodic subscription with firstPayment 5 days from now WHEN daysUntilNextPayment THEN returns 5`() {
        // subscription hasn't started yet; next payment is the first payment itself
        val firstPayment = today.plus(DatePeriod(days = 5))
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = firstPayment,
                type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = 5),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(5)
    }
}
