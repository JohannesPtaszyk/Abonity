package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestPaymentInfo
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Currency
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class AddViewModelTest {

    @BeforeEach
    fun setUp() {
        Locale.setDefault(Locale("de", "DE"))
    }

    @Test
    fun `GIVEN id argument WHEN init THEN state is updated with existing subscription`() {
        runTest {
            val subscription = createTestSubscription()
            val repository = FakeSubscriptionRepository(subscriptionFlow = flowOf(subscription))
            val savedStateHandle = SavedStateHandle(
                mapOf(AddScreenDestination.Args.SUBSCRIPTION_ID_KEY to subscription.id.value),
            )
            val tested = AddViewModel(savedStateHandle, FakeClock(), repository)

            tested.state.test {
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = true))
                assertThat(awaitItem()).isEqualTo(
                    AddState(
                        showNameAsTitle = true,
                        input = AddFormState(
                            paymentDateEpochMillis = TimeUnit.DAYS.toMillis(
                                LocalDate(
                                    2020,
                                    2,
                                    2,
                                ).toEpochDays().toLong(),
                            ),
                            name = ValidatedInput("Test Subscription"),
                            description = "Test Description",
                            priceValue = ValidatedInput("9.99"),
                            currency = Currency.getInstance("EUR"),
                            isOneTimePayment = false,
                            paymentPeriod = PaymentPeriod.MONTHS,
                            paymentPeriodCount = ValidatedInput("1"),
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
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val priceValue = "9.99"
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.priceValue.value).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPrice with comma THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val priceValue = "9,99"
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.priceValue.value).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPeriodic true THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

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
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

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
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                tested.setPaymentPeriodCount("")
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.paymentPeriodCount).isEqualTo(
                    ValidatedInput(
                        "",
                        errors = persistentListOf(ValidationError.EmptyOrBlank),
                    ),
                )
            }
        }
    }

    @TestFactory
    fun `GIVEN no input WHEN setPaymentPeriod THEN input state is updated`(): List<DynamicTest> {
        return PaymentPeriod.entries.map { period ->
            dynamicTest(
                "GIVEN no input WHEN setPaymentPeriod: $period THEN input state is updated",
            ) {
                runTest {
                    val repository = FakeSubscriptionRepository()
                    val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

                    tested.state.test {
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
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

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
                        name = ValidatedInput("Test Subscription"),
                        description = "Test Description",
                        priceValue = ValidatedInput("9.99"),
                        currency = Currency.getInstance("EUR"),
                        isOneTimePayment = false,
                        paymentPeriod = PaymentPeriod.MONTHS,
                        paymentPeriodCount = ValidatedInput("1"),
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
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

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
                        name = ValidatedInput("Test Subscription"),
                        description = "Test Description",
                        priceValue = ValidatedInput("9.99"),
                        currency = Currency.getInstance("EUR"),
                        isOneTimePayment = true,
                        paymentPeriod = PaymentPeriod.MONTHS,
                        paymentPeriodCount = ValidatedInput("1"),
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

    @Test
    fun `GIVEN epochMilliseconds WHEN setPaymentDate THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val epochMilliseconds = 1643458800000 // Replace with your desired value
                tested.setPaymentDate(epochMilliseconds)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.paymentDateEpochMillis).isEqualTo(epochMilliseconds)
            }
        }
    }

    @Test
    fun `GIVEN description WHEN setDescription THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val description = "New Test Description"
                tested.setDescription(description)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.description).isEqualTo(description)
            }
        }
    }

    @Test
    fun `GIVEN currency WHEN setCurrency THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val currency = Currency.getInstance("USD")
                tested.setCurrency(currency)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.currency).isEqualTo(currency)
            }
        }
    }

    @Test
    fun `GIVEN empty name WHEN save THEN validation error is present`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                tested.save()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.name.errors).contains(ValidationError.EmptyOrBlank)
            }
        }
    }

    @Test
    fun `GIVEN empty name WHEN setName THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val name = ""
                tested.setName(name)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.name).isEqualTo(
                    ValidatedInput(
                        "",
                        errors = persistentListOf(ValidationError.EmptyOrBlank),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN empty price WHEN setPrice THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val priceValue = ""
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.priceValue).isEqualTo(
                    ValidatedInput(
                        "",
                        errors = persistentListOf(ValidationError.EmptyOrBlank),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN non-positive period count WHEN setPaymentPeriodCount THEN input state is updated`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val periodCount = "0"
                tested.setPaymentPeriodCount(periodCount)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                assertThat(awaitItem().input.paymentPeriodCount).isEqualTo(
                    ValidatedInput(
                        "0",
                        errors = persistentListOf(ValidationError.MustBePositiveValue),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN invalid name WHEN save THEN validation error is present`() {
        runTest {
            val repository = FakeSubscriptionRepository()
            val tested = AddViewModel(SavedStateHandle(), FakeClock(), repository)

            tested.state.test {
                val invalidName = "   "
                tested.setName(invalidName)
                tested.save()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState(loading = false))
                val item = awaitItem()
                assertThat(item.input.name.errors).contains(ValidationError.EmptyOrBlank)
                assertThat(item.savingState).isEqualTo(AddState.SavingState.ERROR)
            }
        }
    }
}
