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
    fun `GIVEN periodic subscription WHEN daysUntilNextPayment THEN uses next occurrence from today`() {
        // calculator uses FakeClock internally; next payment = today + 1 month
        val expectedNextPayment = today.plus(DatePeriod(months = 1))
        val expectedDays = (expectedNextPayment.toEpochDays() - todayEpochDays).toInt()
        val subscription = createTestSubscription(
            paymentInfo = createTestPaymentInfo(
                firstPayment = today,
                type = PaymentType.Periodic(1, PaymentPeriod.MONTHS),
            ),
            notificationConfig = NotificationConfig(daysBeforePayment = expectedDays),
        )

        val result = daysUntilNextPayment(subscription, todayEpochDays, calculator)

        assertThat(result).isEqualTo(expectedDays)
    }
}
