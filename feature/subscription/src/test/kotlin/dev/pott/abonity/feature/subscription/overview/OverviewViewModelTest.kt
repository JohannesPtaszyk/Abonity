package dev.pott.abonity.feature.subscription.overview

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.PaymentDateCalculator
import dev.pott.abonity.core.domain.PaymentInfoCalculator
import dev.pott.abonity.core.domain.usecase.GetSubscriptionsWithPeriodPrice
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.FakeSubscriptionRepository
import dev.pott.abonity.core.test.entities.createTestSubscription
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@ExtendWith(CoroutinesTestExtension::class)
class OverviewViewModelTest {
    @Test
    fun `GIVEN local subscriptions WHEN initializing THEN return list of overview items AND period prices`() {
        runTest {
            val subscription =
                createTestSubscription(
                    paymentInfo =
                    PaymentInfo(
                        price = Price(1.0, Currency.getInstance("EUR")),
                        firstPayment = LocalDate(2021, 1, 1),
                        type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                    ),
                )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val dateCalculator = PaymentDateCalculator(clock)
            val infoCalculator = PaymentInfoCalculator(dateCalculator, clock)
            val useCase =
                GetSubscriptionsWithPeriodPrice(repository, infoCalculator, dateCalculator)

            val tested = OverviewViewModel(useCase, infoCalculator)

            val expectedSubscriptions = persistentListOf(
                SubscriptionWithPeriodInfo(
                    subscription = subscription,
                    periodPrice = Price(15.0, Currency.getInstance("EUR")),
                    nextPaymentDate = LocalDate(2021, 3, 3),
                ),
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(OverviewState())
                assertThat(awaitItem()).isEqualTo(
                    OverviewState(
                        periodSubscriptions = expectedSubscriptions,
                        periodPrices = persistentListOf(Price(15.0, Currency.getInstance("EUR"))),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN initialized vm WHEN openDetails THEN state contains detail id`() {
        runTest {
            val subscription =
                createTestSubscription(
                    paymentInfo =
                    PaymentInfo(
                        price = Price(1.0, Currency.getInstance("EUR")),
                        firstPayment = LocalDate(2021, 1, 1),
                        type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                    ),
                )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val dateCalculator = PaymentDateCalculator(clock)
            val infoCalculator = PaymentInfoCalculator(dateCalculator, clock)
            val useCase =
                GetSubscriptionsWithPeriodPrice(repository, infoCalculator, dateCalculator)

            val tested = OverviewViewModel(useCase, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                assertThat(awaitItem()).isEqualTo(
                    OverviewState(
                        subscription.id,
                        persistentListOf(
                            SubscriptionWithPeriodInfo(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR"),
                                ),
                                nextPaymentDate = LocalDate(2021, 3, 3),
                            ),
                        ),
                        persistentListOf(Price(15.0, Currency.getInstance("EUR"))),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN initialized vm WHEN openDetails AND consumeDetails THEN state contains no id`() {
        runTest {
            val subscription =
                createTestSubscription(
                    paymentInfo =
                    PaymentInfo(
                        price = Price(1.0, Currency.getInstance("EUR")),
                        firstPayment = LocalDate(2021, 1, 1),
                        type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                    ),
                )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val dateCalculator = PaymentDateCalculator(clock)
            val infoCalculator = PaymentInfoCalculator(dateCalculator, clock)
            val useCase =
                GetSubscriptionsWithPeriodPrice(repository, infoCalculator, dateCalculator)

            val tested = OverviewViewModel(useCase, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                skipItems(1) // Skip emit with id
                tested.consumeDetails()
                assertThat(awaitItem()).isEqualTo(
                    OverviewState(
                        periodSubscriptions = persistentListOf(
                            SubscriptionWithPeriodInfo(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR"),
                                ),
                                nextPaymentDate = LocalDate(2021, 3, 3),
                            ),
                        ),
                        periodPrices = persistentListOf(Price(15.0, Currency.getInstance("EUR"))),
                    ),
                )
            }
        }
    }
}
