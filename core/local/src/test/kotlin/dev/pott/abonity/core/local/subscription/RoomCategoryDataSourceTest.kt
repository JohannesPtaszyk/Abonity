package dev.pott.abonity.core.local.subscription

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import dev.pott.abonity.core.local.fakes.FakeCategoryDao
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RoomCategoryDataSourceTest {

    @Test
    fun `GIVEN category WHEN addOrUpdateCategory THEN return category with id`() =
        runTest {
            val category = CategoryEntity(
                id = 1,
                name = "Name",
            )
            val dao = FakeCategoryDao(initialEntities = listOf(category))

            val tested = RoomCategoryDataSource(dao)

            val result = tested.addOrUpdateCategory(Category(id = CategoryId(1), name = "Name"))
            assertThat(result).isEqualTo(Category(id = CategoryId(1), name = "Name"))
        }

    @Test
    fun `GIVEN categories WHEN getCategoriesFlow THEN return flow of categories`() =
        runTest {
            val categories = listOf(
                CategoryEntity(
                    id = 1,
                    name = "Name",
                ),
                CategoryEntity(
                    id = 2,
                    name = "Name 2",
                ),
            )
            val dao = FakeCategoryDao(initialEntities = categories)

            val tested = RoomCategoryDataSource(dao)

            tested.getCategoriesFlow().test {
                assertThat(awaitItem()).isEqualTo(
                    listOf(
                        Category(id = CategoryId(1), name = "Name"),
                        Category(id = CategoryId(2), name = "Name 2"),
                    ),
                )
            }
        }

    @Test
    fun `GIVEN category id WHEN deleteCategory THEN delete category`() =
        runTest {
            val categories = listOf(
                CategoryEntity(
                    id = 1,
                    name = "Name",
                ),
                CategoryEntity(
                    id = 2,
                    name = "Name 2",
                ),
            )
            val dao = FakeCategoryDao(initialEntities = categories)

            val tested = RoomCategoryDataSource(dao)
            tested.deleteCategory(CategoryId(1))

            dao.getCategoriesFlow().test {
                assertThat(awaitItem()).isEqualTo(
                    listOf(
                        CategoryEntity(
                            id = 2,
                            name = "Name 2",
                        ),
                    ),
                )
            }
        }
}
