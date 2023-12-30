package dev.pott.abonity.core.test.notification

import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.coroutines.flow.Flow

class FakeNotificationTeaserRepository(
    private val flow: Flow<NotificationTeaser>,
) : NotificationTeaserRepository {

    var closedCount: Int = 0

    override fun getNotificationTeaserFlow(): Flow<NotificationTeaser> {
        return flow
    }

    override suspend fun closeTeaser(shouldNotShowAgain: Boolean) {
        closedCount++
    }
}
