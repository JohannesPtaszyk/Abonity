package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.PaymentPeriod
import dev.pott.abonity.core.entity.PaymentType
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.FakeSubscriptionRepository
import dev.pott.abonity.core.test.entities.createTestPaymentInfo
import dev.pott.abonity.core.test.entities.createTestSubscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class AddScreenViewModelTest {

    @Test
    fun `GIVEN id argument WHEN init THEN state is updated with existing subscription`() {
        runTest {
            val subscription = createTestSubscription()
            val repository = FakeSubscriptionRepository(subscriptionFlow = flowOf(subscription))
            val savedStateHandle = SavedStateHandle(
                mapOf(AddScreenDestination.Args.SUBSCRIPTION_ID_KEY to subscription.id.value),
            )
            val tested = createViewModel(
                repository = repository,
                savedStateHandle = savedStateHandle,
            )

            tested.state.test {
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = true))
                assertThat(awaitItem()).isEqualTo(
                    AddState(
                        input = AddFormState(
                            paymentDateEpochMillis = TimeUnit.DAYS.toMillis(
                                LocalDate(
                                    2020,
                                    2,
                                    2,
                                ).toEpochDays().toLong(),
                            ),
                            name = "Test Subscription",
                            description = "Test Description",
                            priceValue = "9.99",
                            currency = Currency.getInstance("EUR"),
                            isOneTimePayment = false,
                            paymentPeriod = PaymentPeriod.MONTHS,
                            paymentPeriodCount = 1,
                        ),
                        loading = true,
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPrice with dot THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            tested.state.test {
                val priceValue = "9.99"
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.priceValue).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPrice with comma THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            tested.state.test {
                val priceValue = "9,99"
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.priceValue).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPeriodic true THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            tested.state.test {
                tested.setPeriodic(true)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.isOneTimePayment).isEqualTo(false)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPeriodic false THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            tested.state.test {
                tested.setPeriodic(false)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.isOneTimePayment).isEqualTo(true)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPaymentPeriodCount empty THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            tested.state.test {
                tested.setPaymentPeriodCount("")
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.paymentPeriodCount).isEqualTo(null)
            }
        }
    }

    @TestFactory
    fun `GIVEN no input WHEN setPaymentPeriod THEN input state is updated`(): List<DynamicTest> {
        return PaymentPeriod.entries.map {
            dynamicTest(
                "GIVEN no input WHEN setPaymentPeriod: $it THEN input state is updated",
            ) {
                runTest {
                    val repository = FakeSubscriptionRepository()
                    val tested = createViewModel(repository = repository)

                    tested.state.test {
                        val period = PaymentPeriod.WEEKS
                        tested.setPaymentPeriod(period)
                        runCurrent()
                        assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                        assertThat(awaitItem().input.paymentPeriod).isEqualTo(period)
                    }
                }
            }
        }
    }

    @Test
    fun `GIVEN full periodic input WHEN save THEN periodic subscription is created`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            val now = FakeClock().now()
            val nowEpochMilliseconds = now.toEpochMilliseconds()
            val name = "Test Subscription"
            val description = "Test Description"
            val priceValue = "9.99"
            val currency = Currency.getInstance("EUR")
            val isOneTime = false
            val paymentPeriod = PaymentPeriod.MONTHS
            val paymentPeriodCount = "1"
            tested.run {
                setPaymentDate(nowEpochMilliseconds)
                setName(name)
                setDescription(description)
                setPrice(priceValue)
                setCurrency(currency)
                setPeriodic(!isOneTime)
                setPaymentPeriod(paymentPeriod)
                setPaymentPeriodCount(paymentPeriodCount)
                save()
                runCurrent()
            }

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input).isEqualTo(
                    AddFormState(
                        paymentDateEpochMillis = nowEpochMilliseconds,
                        name = "Test Subscription",
                        description = "Test Description",
                        priceValue = "9.99",
                        currency = Currency.getInstance("EUR"),
                        isOneTimePayment = false,
                        paymentPeriod = PaymentPeriod.MONTHS,
                        paymentPeriodCount = 1,
                    ),
                )
                cancelAndConsumeRemainingEvents()
            }

            assertThat(repository.addedSubscriptions).isEqualTo(
                listOf(
                    createTestSubscription(
                        id = 0,
                        paymentInfo = createTestPaymentInfo(
                            firstPayment = now.toLocalDateTime(
                                TimeZone.currentSystemDefault(),
                            ).date,
                        ),
                    ),
                ),
            )
        }
    }

    @Test
    fun `GIVEN full one time input WHEN save THEN periodic subscription is created`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = createViewModel(repository = repository)

            val now = FakeClock().now()
            val nowEpochMilliseconds = now.toEpochMilliseconds()
            val name = "Test Subscription"
            val description = "Test Description"
            val priceValue = "9.99"
            val currency = Currency.getInstance("EUR")
            val isOneTime = true
            tested.run {
                setPaymentDate(nowEpochMilliseconds)
                setName(name)
                setDescription(description)
                setPrice(priceValue)
                setCurrency(currency)
                setPeriodic(!isOneTime)
                save()
                runCurrent()
            }

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input).isEqualTo(
                    AddFormState(
                        paymentDateEpochMillis = nowEpochMilliseconds,
                        name = "Test Subscription",
                        description = "Test Description",
                        priceValue = "9.99",
                        currency = Currency.getInstance("EUR"),
                        isOneTimePayment = true,
                        paymentPeriod = null,
                        paymentPeriodCount = null,
                    ),
                )
                cancelAndConsumeRemainingEvents()
            }

            assertThat(repository.addedSubscriptions).isEqualTo(
                listOf(
                    createTestSubscription(
                        id = 0,
                        paymentInfo = createTestPaymentInfo(
                            firstPayment = now.toLocalDateTime(
                                TimeZone.currentSystemDefault(),
                            ).date,
                            type = PaymentType.OneTime,
                        ),
                    ),
                ),
            )
        }
    }

    private fun createViewModel(
        clock: Clock = FakeClock(),
        repository: SubscriptionRepository = FakeSubscriptionRepository(),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): AddScreenViewModel {
        return AddScreenViewModel(savedStateHandle, clock, repository)
    }
}
