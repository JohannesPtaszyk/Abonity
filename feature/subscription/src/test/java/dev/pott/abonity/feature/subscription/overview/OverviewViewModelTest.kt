package dev.pott.abonity.feature.subscription.overview

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.FakeClock
import dev.pott.abonity.FakeSubscriptionRepository
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.PeriodicPriceCalculator
import dev.pott.abonity.core.entity.PaymentInfo
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.entities.createTestSubscription
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@ExtendWith(CoroutinesTestExtension::class)
class OverviewViewModelTest {

    @Test
    fun `GIVEN local subscriptions WHEN initializing THEN return list of overview items`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS)
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val calculator = createPeriodicPriceCalculator()

            val tested = OverviewViewModel(repository, calculator)

            val expectedSubscriptions = listOf(
                SelectableSubscriptionWithPeriodPrice(
                    subscription = subscription,
                    periodPrice = Price(15.0, Currency.getInstance("EUR")),
                    isSelected = false,
                )
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(OverviewState())
                assertThat(awaitItem()).isEqualTo(OverviewState(
                    periodSubscriptions = expectedSubscriptions,
                    periodPrices = listOf(Price(15.0, Currency.getInstance("EUR")))
                ))
            }
        }
    }

    @Test
    fun `GIVEN initialized vm WHEN openDetails THEN state contains detail id`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS)
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val calculator = createPeriodicPriceCalculator()

            val tested = OverviewViewModel(repository, calculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                assertThat(awaitItem()).isEqualTo(
                    OverviewState(
                        subscription.id,
                        listOf(
                            SelectableSubscriptionWithPeriodPrice(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR")
                                ),
                                isSelected = true
                            )
                        ),
                        listOf(Price(15.0, Currency.getInstance("EUR")))
                    )
                )
            }
        }
    }

    @Test
    fun `GIVEN initialized vm WHEN openDetails AND consumeDetails THEN state contains no id`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS)
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val calculator = createPeriodicPriceCalculator()

            val tested = OverviewViewModel(repository, calculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                skipItems(1) // Skip emit with id
                tested.consumeDetails()
                assertThat(awaitItem()).isEqualTo(
                    OverviewState(
                        periodSubscriptions = listOf(
                            SelectableSubscriptionWithPeriodPrice(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR")
                                ),
                                isSelected = false
                            )
                        ),
                        periodPrices = listOf(Price(15.0, Currency.getInstance("EUR")))
                    )
                )
            }
        }
    }

    private fun createPeriodicPriceCalculator(): PeriodicPriceCalculator {
        val today = Instant.parse("2021-03-01T00:00:00Z")
        val clock = FakeClock(today)
        return PeriodicPriceCalculator(clock)
    }
}
