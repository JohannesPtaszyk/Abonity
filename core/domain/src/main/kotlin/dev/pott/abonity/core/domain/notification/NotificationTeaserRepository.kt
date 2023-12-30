package dev.pott.abonity.core.domain.notification

import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.coroutines.flow.Flow

interface NotificationTeaserRepository {
    fun getNotificationTeaserFlow(): Flow<NotificationTeaser>

    suspend fun closeTeaser(shouldNotShowAgain: Boolean)
}
