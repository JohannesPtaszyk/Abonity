package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.usecase.GetSubscriptionsWithPeriodPrice
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.ui.components.subscription.SubscriptionFilterState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@ExtendWith(CoroutinesTestExtension::class)
class OverviewViewModelTest {
    @Test
    fun `GIVEN local subscriptions WHEN initializing THEN return list of overview items AND period prices`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase = GetSubscriptionsWithPeriodPrice(repository, infoCalculator)

            val tested = OverviewViewModel(SavedStateHandle(), useCase, clock, infoCalculator)

            val expectedSubscriptions = persistentListOf(
                SubscriptionWithPeriodInfo(
                    subscription = subscription,
                    periodPrice = Price(15.0, Currency.getInstance("EUR")),
                    nextPaymentDate = LocalDate(2021, 3, 2),
                ),
            )

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(OverviewState.Loading)
                assertThat(awaitItem()).isEqualTo(
                    OverviewState.Loaded(
                        subscriptions = expectedSubscriptions,
                        filterState = SubscriptionFilterState(
                            listOf(Price(15.0, Currency.getInstance("EUR"))),
                            period = PaymentPeriod.MONTHS,
                            selectedItems = emptyList(),
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN id argument WHEN initializing THEN id is preselected`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase = GetSubscriptionsWithPeriodPrice(repository, infoCalculator)
            val savedStateHandle = SavedStateHandle(
                mapOf(OverviewScreenDestination.Args.DETAIL_ID_KEY to subscription.id.value),
            )
            val tested = OverviewViewModel(savedStateHandle, useCase, clock, infoCalculator)

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(OverviewState.Loading)
                assertThat(
                    (awaitItem() as OverviewState.Loaded).detailId,
                ).isEqualTo(subscription.id)
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
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase = GetSubscriptionsWithPeriodPrice(repository, infoCalculator)

            val tested = OverviewViewModel(SavedStateHandle(), useCase, clock, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                assertThat(awaitItem()).isEqualTo(
                    OverviewState.Loaded(
                        subscriptions = persistentListOf(
                            SubscriptionWithPeriodInfo(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR"),
                                ),
                                nextPaymentDate = LocalDate(2021, 3, 2),
                            ),
                        ),
                        filterState = SubscriptionFilterState(
                            listOf(Price(15.0, Currency.getInstance("EUR"))),
                            period = PaymentPeriod.MONTHS,
                            selectedItems = emptyList(),
                        ),
                        subscription.id,
                    ),
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
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase =
                GetSubscriptionsWithPeriodPrice(repository, infoCalculator)

            val tested = OverviewViewModel(SavedStateHandle(), useCase, clock, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                tested.openDetails(subscription.id)
                skipItems(1) // Skip emit with id
                tested.consumeDetails()
                assertThat(awaitItem()).isEqualTo(
                    OverviewState.Loaded(
                        subscriptions = persistentListOf(
                            SubscriptionWithPeriodInfo(
                                subscription = subscription,
                                periodPrice = Price(
                                    15.0,
                                    Currency.getInstance("EUR"),
                                ),
                                nextPaymentDate = LocalDate(2021, 3, 2),
                            ),
                        ),
                        filterState = SubscriptionFilterState(
                            listOf(Price(15.0, Currency.getInstance("EUR"))),
                            period = PaymentPeriod.MONTHS,
                            selectedItems = emptyList(),
                        ),
                    ),
                )
            }
        }
    }

    @Test
    @Disabled("Need to extract use case and fix dispatcher for filtering")
    fun `GIVEN initialized vm WHEN toggleFilter THEN state contains updated filter items`() {
        runTest {
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase =
                GetSubscriptionsWithPeriodPrice(repository, infoCalculator)

            val tested = OverviewViewModel(SavedStateHandle(), useCase, clock, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                val initialFilterState = (awaitItem() as OverviewState.Loaded).filterState
                val initialFilterItems = initialFilterState.selectedItems

                tested.toggleFilter(
                    SubscriptionFilterItem.Currency(Price(1.0, Currency.getInstance("EUR"))),
                )
                val updatedFilterState = (awaitItem() as OverviewState.Loaded).filterState
                val updatedFilterItems = updatedFilterState.selectedItems

                assertThat(updatedFilterItems).isNotEmpty()
                assertThat(updatedFilterItems).isNotEqualTo(initialFilterItems)
            }
        }
    }

    @Test
    @Disabled("Need to extract use case and fix dispatcher for filtering")
    fun `GIVEN initialized vm WHEN toggleFilter THEN state contains updated subscriptions`() {
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
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription1, subscription2))
            val repository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase = GetSubscriptionsWithPeriodPrice(repository, infoCalculator)

            val tested = OverviewViewModel(SavedStateHandle(), useCase, clock, infoCalculator)

            tested.state.test {
                skipItems(2) // Skip initial state and first subscription emit
                val initialSubscriptions = (awaitItem() as OverviewState.Loaded).subscriptions

                tested.toggleFilter(
                    SubscriptionFilterItem.Currency(Price(1.0, Currency.getInstance("EUR"))),
                )
                val updatedSubscriptions = (awaitItem() as OverviewState.Loaded).subscriptions

                assertThat(updatedSubscriptions).isNotEqualTo(initialSubscriptions)
            }
        }
    }
}
