package dev.pott.abonity.core.test.notification

import dev.pott.abonity.core.domain.notification.NotificationTeaserLocalDataSource
import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeNotificationTeaserLocalDataSource(
    private val flow: Flow<NotificationTeaser> = emptyFlow(),
) : NotificationTeaserLocalDataSource {

    var closedCount: Int = 0

    override fun getNotificationTeaserFlow(): Flow<NotificationTeaser> = flow

    override suspend fun closeTeaser(shouldNotShowAgain: Boolean) {
        closedCount++
    }
}
