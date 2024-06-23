package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.common.test.InjectTestDispatcher
import dev.pott.abonity.core.domain.subscription.PaymentInfoCalculator
import dev.pott.abonity.core.domain.subscription.usecase.GetSubscriptionsWithFilterUseCase
import dev.pott.abonity.core.domain.subscription.usecase.GetSubscriptionsWithPeriodPrice
import dev.pott.abonity.core.entity.subscription.PaymentInfo
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.entity.subscription.Price
import dev.pott.abonity.core.entity.subscription.SubscriptionFilter
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.entity.subscription.SubscriptionWithPeriodInfo
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.settings.FakeSettingsRepository
import dev.pott.abonity.core.test.settings.entities.createTestSettings
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class OverviewViewModelTest {

    @InjectTestDispatcher
    lateinit var dispatcher: CoroutineDispatcher

    @Test
    fun `GIVEN local subscriptions WHEN initializing THEN return list of overview items AND period prices`() {
        runTest {
            runCurrent()
            val subscription = createTestSubscription(
                paymentInfo = PaymentInfo(
                    price = Price(1.0, Currency.getInstance("EUR")),
                    firstPayment = LocalDate(2021, 1, 1),
                    type = PaymentType.Periodic(2, PaymentPeriod.DAYS),
                ),
            )
            val localSubscriptionFlow = flowOf(listOf(subscription))
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                SavedStateHandle(),
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

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
                        filter = SubscriptionFilter(
                            items = listOf(
                                SubscriptionFilterItem.CurrentPeriod(PaymentPeriod.MONTHS),
                                SubscriptionFilterItem.Currency(
                                    Price(15.0, Currency.getInstance("EUR")),
                                ),
                            ),
                            selectedItems = emptyList(),
                        ),
                        currentPeriod = settings.period,
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
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val settingsRepository = FakeSettingsRepository(createTestSettings())
            val savedStateHandle = SavedStateHandle(
                mapOf(OverviewScreenDestination.Args.DETAIL_ID_KEY to subscription.id.value),
            )
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                savedStateHandle,
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

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
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                SavedStateHandle(),
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

            tested.state.test {
                runCurrent()
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
                        filter = SubscriptionFilter(
                            items = listOf(
                                SubscriptionFilterItem.CurrentPeriod(PaymentPeriod.MONTHS),
                                SubscriptionFilterItem.Currency(
                                    Price(15.0, Currency.getInstance("EUR")),
                                ),
                            ),
                            selectedItems = emptyList(),
                        ),
                        currentPeriod = settings.period,
                        detailId = subscription.id,
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
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val settings = createTestSettings()
            val settingsRepository = FakeSettingsRepository(settings)
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                SavedStateHandle(),
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

            tested.state.test {
                runCurrent()
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
                        filter = SubscriptionFilter(
                            items = listOf(
                                SubscriptionFilterItem.CurrentPeriod(PaymentPeriod.MONTHS),
                                SubscriptionFilterItem.Currency(
                                    Price(15.0, Currency.getInstance("EUR")),
                                ),
                            ),
                            selectedItems = emptyList(),
                        ),
                        currentPeriod = settings.period,
                    ),
                )
            }
        }
    }

    @Test
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
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock()
            val infoCalculator = PaymentInfoCalculator(clock)
            val settingsRepository = FakeSettingsRepository(createTestSettings())
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                SavedStateHandle(),
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

            tested.state.test {
                runCurrent()
                skipItems(1) // Skip initial state
                val initialFilterState = (awaitItem() as OverviewState.Loaded).filter
                val initialFilterItems = initialFilterState.selectedItems

                tested.toggleFilter(
                    SubscriptionFilterItem.Currency(Price(1.0, Currency.getInstance("EUR"))),
                )
                runCurrent()

                val updatedFilterState = (awaitItem() as OverviewState.Loaded).filter
                val updatedFilterItems = updatedFilterState.selectedItems
                assertThat(updatedFilterItems).isNotEmpty()
                assertThat(updatedFilterItems).isNotEqualTo(initialFilterItems)
            }
        }
    }

    @Test
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
            val subscriptionRepository = FakeSubscriptionRepository(localSubscriptionFlow)
            val clock = FakeClock(now = Instant.parse("2021-01-01T00:00:00Z"))
            val infoCalculator = PaymentInfoCalculator(clock)
            val settingsRepository = FakeSettingsRepository(createTestSettings())
            val useCase = GetSubscriptionsWithFilterUseCase(
                GetSubscriptionsWithPeriodPrice(
                    subscriptionRepository,
                    settingsRepository,
                    infoCalculator,
                    dispatcher,
                ),
                settingsRepository,
                infoCalculator,
                clock,
                dispatcher,
            )

            val tested = OverviewViewModel(
                SavedStateHandle(),
                useCase,
                settingsRepository,
                subscriptionRepository,
            )

            tested.state.test {
                runCurrent()
                skipItems(1) // Skip initial Loading state
                val initialSubscriptions = (awaitItem() as OverviewState.Loaded).subscriptions

                tested.toggleFilter(
                    SubscriptionFilterItem.Currency(Price(1.0, Currency.getInstance("EUR"))),
                )
                runCurrent()

                val updatedSubscriptions = (awaitItem() as OverviewState.Loaded).subscriptions
                assertThat(updatedSubscriptions).isNotEqualTo(initialSubscriptions)
                assertThat(cancelAndConsumeRemainingEvents()).isEmpty()
            }
        }
    }
}
