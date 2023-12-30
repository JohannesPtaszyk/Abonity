package dev.pott.abonity.core.domain.notification.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.notification.NotificationTeaser
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.notification.FakeNotificationTeaserRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

class ShouldShowNotificationTeaserUseCaseTest {

    private val clock = FakeClock()

    @Test
    fun `GIVEN shouldNotShowAgain is true WHEN invoking use case THEN should not show teaser`() {
        runTest {
            val notificationTeaser =
                NotificationTeaser(lastClosed = null, shouldNotShowAgain = true)
            val repository = FakeNotificationTeaserRepository(flowOf(notificationTeaser))

            val shouldShowTeaserFlow =
                ShouldShowNotificationTeaserUseCase(clock, repository).invoke()

            shouldShowTeaserFlow.test {
                assertThat(awaitItem()).isEqualTo(false)
                awaitComplete()
            }
        }
    }

    @Test
    fun `GIVEN lastClosed is null WHEN invoking use case THEN should show teaser`() {
        runTest {
            val notificationTeaser = NotificationTeaser(
                lastClosed = null,
                shouldNotShowAgain = false,
            )
            val repository = FakeNotificationTeaserRepository(flowOf(notificationTeaser))

            val shouldShowTeaserFlow =
                ShouldShowNotificationTeaserUseCase(clock, repository).invoke()

            shouldShowTeaserFlow.test {
                assertThat(awaitItem()).isEqualTo(true)
                awaitComplete()
            }
        }
    }

    @Test
    fun `GIVEN lastClosed is more than TIME_UNTIL_SHOWN WHEN invoking use case THEN should show teaser`() {
        runTest {
            val lastClosed = (clock.now() - TIME_UNTIL_SHOWN.days).toLocalDateTime(
                TimeZone.currentSystemDefault(),
            )
            val notificationTeaser =
                NotificationTeaser(lastClosed = lastClosed, shouldNotShowAgain = false)
            val repository = FakeNotificationTeaserRepository(flowOf(notificationTeaser))

            val shouldShowTeaserFlow = ShouldShowNotificationTeaserUseCase(
                clock,
                repository,
            ).invoke()

            shouldShowTeaserFlow.test {
                assertThat(awaitItem()).isEqualTo(true)
                awaitComplete()
            }
        }
    }

    @Test
    fun `GIVEN lastClosed less than TIME_UNTIL_SHOWN WHEN invoking use case THEN should not show teaser`() {
        runTest {
            val lastClosed = (clock.now() - 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            val notificationTeaser =
                NotificationTeaser(lastClosed = lastClosed, shouldNotShowAgain = false)
            val repository = FakeNotificationTeaserRepository(flowOf(notificationTeaser))

            val shouldShowTeaserFlow = ShouldShowNotificationTeaserUseCase(
                clock,
                repository,
            ).invoke()

            shouldShowTeaserFlow.test {
                assertThat(awaitItem()).isEqualTo(false)
                awaitComplete()
            }
        }
    }
}
