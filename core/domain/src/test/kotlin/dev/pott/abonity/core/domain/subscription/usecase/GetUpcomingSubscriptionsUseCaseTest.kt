package dev.pott.abonity.core.domain.subscription.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.common.test.InjectTestDispatcher
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.entity.subscription.UpcomingSubscriptions
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.settings.FakeSettingsRepository
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@ExtendWith(CoroutinesTestExtension::class)
class GetUpcomingSubscriptionsUseCaseTest {

    @InjectTestDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    @Test
    fun `GIVEN upcoming subscriptions WHEN invoked THEN return filtered sorted map`() {
        runTest {
            val subscription1 = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val subscription2 = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(2.0, Currency.getInstance("USD")),
                    firstPayment = LocalDate(2021, 2, 1),
                    type = PaymentType.Periodic(1, PaymentPeriod.YEARS),
                ),
            )
            val subscription3 = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(3.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2022, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
            )

            val localSubscriptionFlow = flowOf(listOf(subscription1, subscription2, subscription3))
            val settingsRepository =
                FakeSettingsRepository(createTestSettings(period = PaymentPeriod.MONTHS))
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock(now = Instant.parse("2022-02-01T00:00:00Z"))
            val infoCalculator = PaymentInfoCalculator(clock)

            val tested = GetUpcomingSubscriptionsUseCase(
                clock,
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    testDispatcher,
                ),
                settingsRepository,
                testDispatcher,
            )

            tested().test {
                assertThat(awaitItem()).isEqualTo(
                    UpcomingSubscriptions(
                        subscriptions = mapOf(
                            LocalDate(2022, 2, 1) to listOf(
                                SubscriptionWithPeriodInfo(
                                    subscription = subscription1,
                                    periodPrice = Price(14.0, Currency.getInstance("EUR")),
                                    nextPaymentDate = LocalDate(2022, 2, 1),
                                ),
                                SubscriptionWithPeriodInfo(
                                    subscription = subscription2,
                                    periodPrice = Price(2.0, Currency.getInstance("USD")),
                                    nextPaymentDate = LocalDate(2022, 2, 1),
                                ),
                            ),
                        ),
                        hasAnySubscriptions = true,
                        period = PaymentPeriod.MONTHS,
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN no upcoming subscriptions WHEN invoked THEN return empty list`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.MONTHS),
                ),
            )

            val localSubscriptionFlow = flowOf(listOf(subscription))
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val settingsRepository =
                FakeSettingsRepository(createTestSettings(period = PaymentPeriod.MONTHS))
            val clock = FakeClock(now = Instant.parse("2022-02-02T00:00:00Z"))
            val infoCalculator = PaymentInfoCalculator(clock)

            val tested = GetUpcomingSubscriptionsUseCase(
                clock,
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    testDispatcher,
                ),
                settingsRepository,
                testDispatcher,
            )

            tested().test {
                assertThat(awaitItem()).isEqualTo(
                    UpcomingSubscriptions(
                        subscriptions = emptyMap(),
                        true,
                        PaymentPeriod.MONTHS,
                    ),
                )
            }
        }
    }
}
