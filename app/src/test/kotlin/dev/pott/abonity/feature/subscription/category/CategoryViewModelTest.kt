package dev.pott.abonity.feature.subscription.category

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.test.CoroutinesTestExtension
import dev.pott.abonity.core.test.subscription.FakeCategoryRepository
import dev.pott.abonity.core.test.subscription.entities.createTestCategories
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class CategoryViewModelTest {

    @Test
    fun `GIVEN categories WHEN state is observed THEN categories are loaded`() =
        runTest {
            val categories = createTestCategories(10)
            val categoryRepository = FakeCategoryRepository(flowOf(categories))

            val tested = CategoryViewModel(categoryRepository)

            tested.state.test {
                assertThat(awaitItem()).isEqualTo(CategoryState.Loading)
                assertThat(awaitItem()).isEqualTo(
                    CategoryState.Loaded(
                        categories.toImmutableList(),
                        persistentListOf(),
                        deleteEnabled = false,
                        isDeleting = false,
                    ),
                )
            }
        }

    @Test
    fun `GIVEN categories WHEN category is selected THEN selected categories are updated`() {
        runTest {
            val categories = createTestCategories(10)
            val categoryRepository = FakeCategoryRepository(flowOf(categories))

            val tested = CategoryViewModel(categoryRepository)

            tested.state.test {
                skipItems(2) // Initial state and loaded state

                tested.selectCategory(categories[0])

                assertThat(awaitItem()).isEqualTo(
                    CategoryState.Loaded(
                        categories.toImmutableList(),
                        persistentListOf(categories[0]),
                        deleteEnabled = true,
                        isDeleting = false,
                    ),
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN selected categories WHEN delete is called THEN selected categories are deleted`() {
        runTest {
            val categories = createTestCategories(10)
            val categoryRepository = FakeCategoryRepository(flowOf(categories))

            val tested = CategoryViewModel(categoryRepository)

            tested.state.test {
                skipItems(2) // Initial state and loaded state

                tested.selectCategory(categories[0])
                tested.selectCategory(categories[1])

                tested.deleteSelected()
                runCurrent()

                assertThat(awaitItem()).isEqualTo(
                    CategoryState.Loaded(
                        categories.toImmutableList(),
                        listOf(categories[0], categories[1]).toImmutableList(),
                        deleteEnabled = false,
                        isDeleting = true,
                    ),
                )
                assertThat(awaitItem()).isEqualTo(
                    CategoryState.Loaded(
                        categories.toImmutableList(),
                        persistentListOf(),
                        deleteEnabled = false,
                        isDeleting = false,
                    ),
                )
            }
        }
    }
}
