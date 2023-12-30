package dev.pott.abonity.core.domain.notification

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.core.test.notification.FakeNotificationTeaserLocalDataSource
import dev.pott.abonity.core.test.notification.entities.createTestNotificationTeaser
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class NotificationTeaserRepositoryImplTest {

    @Test
    fun `GIVEN a local flow WHEN getNotificationTeaserFlow THEN return local flow`() {
        val localFlow = flowOf(createTestNotificationTeaser())
        val localDataSource = FakeNotificationTeaserLocalDataSource(localFlow)

        val tested = NotificationTeaserRepositoryImpl(localDataSource)

        assertThat(tested.getNotificationTeaserFlow()).isEqualTo(localFlow)
    }

    @Test
    fun `WHEN closeTeaser THEN teaser is closed once`() =
        runTest {
            val localDataSource = FakeNotificationTeaserLocalDataSource()

            NotificationTeaserRepositoryImpl(localDataSource).closeTeaser(true)

            assertThat(localDataSource.closedCount).isEqualTo(1)
        }
}
