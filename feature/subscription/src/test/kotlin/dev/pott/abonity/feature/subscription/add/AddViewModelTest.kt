package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.PaymentType
import dev.pott.abonity.core.test.FakeClock
import dev.pott.abonity.core.test.subscription.FakeCategoryRepository
import dev.pott.abonity.core.test.subscription.FakeSubscriptionRepository
import dev.pott.abonity.core.test.subscription.entities.createTestCategories
import dev.pott.abonity.core.test.subscription.entities.createTestCategory
import dev.pott.abonity.core.test.subscription.entities.createTestPaymentInfo
import dev.pott.abonity.core.test.subscription.entities.createTestSubscription
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import java.util.Currency
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(RobolectricExtension::class, CoroutinesTestExtension::class)
class AddViewModelTest {

    @BeforeEach
    fun setUp() {
        Locale.setDefault(Locale("de", "DE"))
    }

    @Test
    fun `GIVEN id argument WHEN init THEN state is updated with existing subscription`() {
        runTest {
            val subscription = createTestSubscription()
            val subscriptionRepository =
                FakeSubscriptionRepository(subscriptionFlow = flowOf(subscription))
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val savedStateHandle = SavedStateHandle(route = AddDestination(subscription.id.value))
            val tested = AddViewModel(
                savedStateHandle,
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem()).isEqualTo(
                    AddState(
                        showNameAsTitle = true,
                        categories = persistentListOf(createTestCategory()),
                        formState = AddFormState(
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
                            selectedCategories = persistentListOf(createTestCategory()),
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPrice with dot THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val priceValue = "9.99"
                tested.setPrice(priceValue)
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.priceValue.value).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPrice with comma THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val priceValue = "9,99"
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.priceValue.value).isEqualTo(priceValue)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPeriodic true THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.setPeriodic(true)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.isOneTimePayment).isEqualTo(false)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPeriodic false THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.setPeriodic(false)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.isOneTimePayment).isEqualTo(true)
            }
        }
    }

    @Test
    fun `GIVEN no input WHEN setPaymentPeriodCount empty THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.setPaymentPeriodCount("")
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.paymentPeriodCount).isEqualTo(
                    ValidatedInput(
                        "",
                        errors = persistentListOf(ValidationError.EmptyOrBlank),
                    ),
                )
            }
        }
    }

    @TestFactory
    fun `GIVEN no input WHEN setPaymentPeriod THEN input state is updated`(): List<DynamicTest> =
        PaymentPeriod.entries.map { period ->
            dynamicTest(
                "GIVEN no input WHEN setPaymentPeriod: $period THEN input state is updated",
            ) {
                runTest {
                    val subscriptionRepository = FakeSubscriptionRepository()
                    val categoryRepository =
                        FakeCategoryRepository(flowOf(listOf(createTestCategory())))
                    val tested = AddViewModel(
                        SavedStateHandle(),
                        FakeClock(),
                        subscriptionRepository,
                        categoryRepository,
                    )

                    tested.state.test {
                        tested.setPaymentPeriod(period)
                        runCurrent()
                        assertThat(awaitItem()).isEqualTo(AddState())
                        assertThat(awaitItem().formState.paymentPeriod).isEqualTo(period)
                    }
                }
            }
        }

    @Test
    fun `GIVEN full periodic input WHEN save THEN periodic subscription is created`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val testCategories = createTestCategories(10)
            val categoryRepository = FakeCategoryRepository(flowOf(testCategories))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            val now = FakeClock().now()
            val nowEpochMilliseconds = now.toEpochMilliseconds()
            val name = "Test Subscription"
            val description = "Test Description"
            val priceValue = "9.99"
            val currency = Currency.getInstance("EUR")
            val isOneTime = false
            val paymentPeriod = PaymentPeriod.MONTHS
            val paymentPeriodCount = "1"

            tested.state.test {
                tested.run {
                    setPaymentDate(nowEpochMilliseconds)
                    setName(name)
                    setDescription(description)
                    setPrice(priceValue)
                    setCurrency(currency)
                    setPeriodic(!isOneTime)
                    setPaymentPeriod(paymentPeriod)
                    setPaymentPeriodCount(paymentPeriodCount)
                    selectCategory(testCategories.last())
                    save()
                    runCurrent()
                }
                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem()).isEqualTo(
                    AddState(
                        formState = AddFormState(
                            paymentDateEpochMillis = nowEpochMilliseconds,
                            name = ValidatedInput("Test Subscription"),
                            description = "Test Description",
                            priceValue = ValidatedInput("9.99"),
                            currency = Currency.getInstance("EUR"),
                            isOneTimePayment = false,
                            paymentPeriod = PaymentPeriod.MONTHS,
                            paymentPeriodCount = ValidatedInput("1"),
                            saving = AddState.SavingState.SAVED,
                            selectedCategories = persistentListOf(testCategories.last()),
                        ),
                        categories = buildList {
                            add(testCategories.last())
                            addAll(testCategories.dropLast(1))
                        }.toImmutableList(),
                        showNameAsTitle = false,
                    ),
                )
            }

            assertThat(subscriptionRepository.addedSubscriptions).isEqualTo(
                listOf(
                    createTestSubscription(
                        id = 0,
                        paymentInfo = createTestPaymentInfo(
                            firstPayment = now.toLocalDateTime(
                                TimeZone.currentSystemDefault(),
                            ).date,
                        ),
                        categories = listOf(testCategories.last()),
                    ),
                ),
            )
        }
    }

    @Test
    fun `GIVEN full one time input WHEN save THEN periodic subscription is created`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            val now = FakeClock().now()
            val nowEpochMilliseconds = now.toEpochMilliseconds()
            val name = "Test Subscription"
            val description = "Test Description"
            val priceValue = "9.99"
            val currency = Currency.getInstance("EUR")
            val isOneTime = true

            tested.state.test {
                tested.run {
                    setPaymentDate(nowEpochMilliseconds)
                    setName(name)
                    setDescription(description)
                    setPrice(priceValue)
                    setCurrency(currency)
                    setPeriodic(!isOneTime)
                    selectCategory(createTestCategory())
                    save()
                    runCurrent()
                }
                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState).isEqualTo(
                    AddFormState(
                        paymentDateEpochMillis = nowEpochMilliseconds,
                        name = ValidatedInput("Test Subscription"),
                        description = "Test Description",
                        priceValue = ValidatedInput("9.99"),
                        currency = Currency.getInstance("EUR"),
                        isOneTimePayment = true,
                        paymentPeriod = PaymentPeriod.MONTHS,
                        paymentPeriodCount = ValidatedInput("1"),
                        saving = AddState.SavingState.SAVED,
                        selectedCategories = persistentListOf(createTestCategory()),
                    ),
                )
                cancelAndConsumeRemainingEvents()
            }

            assertThat(subscriptionRepository.addedSubscriptions).isEqualTo(
                listOf(
                    createTestSubscription(
                        id = 0,
                        paymentInfo = createTestPaymentInfo(
                            firstPayment = now.toLocalDateTime(
                                TimeZone.currentSystemDefault(),
                            ).date,
                            type = PaymentType.OneTime,
                        ),
                        categories = listOf(createTestCategory()),
                    ),
                ),
            )
        }
    }

    @Test
    fun `GIVEN epochMilliseconds WHEN setPaymentDate THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val epochMilliseconds = 1643458800000 // Replace with your desired value
                tested.setPaymentDate(epochMilliseconds)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(
                    awaitItem().formState.paymentDateEpochMillis,
                ).isEqualTo(epochMilliseconds)
            }
        }
    }

    @Test
    fun `GIVEN description WHEN setDescription THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val description = "New Test Description"
                tested.setDescription(description)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.description).isEqualTo(description)
            }
        }
    }

    @Test
    fun `GIVEN currency WHEN setCurrency THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val currency = Currency.getInstance("USD")
                tested.setCurrency(currency)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.currency).isEqualTo(currency)
            }
        }
    }

    @Test
    fun `GIVEN empty name WHEN save THEN validation error is present`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.save()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.name.errors).contains(ValidationError.EmptyOrBlank)
            }
        }
    }

    @Test
    fun `GIVEN empty name WHEN setName THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val name = ""
                tested.setName(name)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.name).isEqualTo(
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
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val priceValue = ""
                tested.setPrice(priceValue)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.priceValue).isEqualTo(
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
        runTest(UnconfinedTestDispatcher()) {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val periodCount = "0"
                tested.setPaymentPeriodCount(periodCount)

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.paymentPeriodCount).isEqualTo(
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
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                val invalidName = "   "
                tested.setName(invalidName)
                tested.save()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                val item = awaitItem()
                assertThat(item.formState.name.errors).contains(ValidationError.EmptyOrBlank)
                assertThat(item.formState.saving).isEqualTo(AddState.SavingState.ERROR)
            }
        }
    }

    @Test
    fun `GIVEN category WHEN selectCategory THEN input state is updated`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val category = createTestCategory()
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.selectCategory(category)
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.selectedCategories).contains(category)
            }
        }
    }

    @Test
    fun `GIVEN initial state WHEN openCategorySelection THEN category selection is opened`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.openAddCategoryDialog()
                runCurrent()
                tested.closeAddCategoryDialog()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.showCategoryDialog).isEqualTo(true)
                assertThat(awaitItem().formState.showCategoryDialog).isEqualTo(false)
            }
        }
    }

    @Test
    fun `GIVEN initial state WHEN closeCategorySelection THEN category selection is closed`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.openAddCategoryDialog()
                runCurrent()
                tested.closeAddCategoryDialog()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.showCategoryDialog).isTrue()
                assertThat(awaitItem().formState.showCategoryDialog).isFalse()
            }
        }
    }

    @Test
    fun `GIVEN category WHEN addCategory THEN category is added to selected categories`() {
        runTest {
            val subscriptionRepository = FakeSubscriptionRepository()
            val categoryRepository = FakeCategoryRepository(flowOf(listOf(createTestCategory())))
            val tested = AddViewModel(
                SavedStateHandle(),
                FakeClock(),
                subscriptionRepository,
                categoryRepository,
            )

            tested.state.test {
                tested.openAddCategoryDialog()
                runCurrent()

                tested.addCategory("name")
                runCurrent()

                assertThat(awaitItem()).isEqualTo(AddState())
                assertThat(awaitItem().formState.showCategoryDialog).isTrue()
                val item = awaitItem()
                assertThat(item.formState.selectedCategories).contains(Category(name = "name"))
                assertThat(item.formState.showCategoryDialog).isFalse()
            }
        }
    }
}
