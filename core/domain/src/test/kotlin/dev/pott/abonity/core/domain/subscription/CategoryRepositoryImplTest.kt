package dev.pott.abonity.core.domain.subscription

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import dev.pott.abonity.core.test.subscription.FakeCategoryLocalDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CategoryRepositoryImplTest {

    @Test
    fun `GIVEN a category WHEN addOrUpdateCategory THEN return category`() =
        runTest {
            val category = Category(CategoryId(1), "name")
            val localDataSource = FakeCategoryLocalDataSource(
                testCategoriesFlow = flowOf(listOf(category)),
            )
            val tested = CategoryRepositoryImpl(localDataSource)

            val result = tested.addOrUpdateCategory(category)
            assertThat(result).isEqualTo(category)
            assertThat(localDataSource.addedCategories).isEqualTo(listOf(category))
        }

    @Test
    fun `GIVEN a category WHEN getCategoriesFlow THEN return flow of categories`() =
        runTest {
            val category = Category(CategoryId(1), "name")
            val localDataSource = FakeCategoryLocalDataSource(
                testCategoriesFlow = flowOf(listOf(category)),
            )
            val tested = CategoryRepositoryImpl(localDataSource)

            val result = tested.getCategoriesFlow().toList()
            assertThat(result).isEqualTo(listOf(listOf(category)))
        }

    @Test
    fun `GIVEN a category WHEN deleteCategory THEN delete category`() =
        runTest {
            val categoryId = CategoryId(1)
            val localDataSource = FakeCategoryLocalDataSource()
            val tested = CategoryRepositoryImpl(localDataSource)

            tested.deleteCategory(categoryId)
            assertThat(localDataSource.deletedCategories).isEqualTo(listOf(categoryId))
        }
}
