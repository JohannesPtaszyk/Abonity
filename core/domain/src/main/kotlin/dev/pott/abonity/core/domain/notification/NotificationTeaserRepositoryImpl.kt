package dev.pott.abonity.core.domain.notification

import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationTeaserRepositoryImpl @Inject constructor(
    private val localDataSource: NotificationTeaserLocalDataSource,
) : NotificationTeaserRepository {

    override fun getNotificationTeaserFlow(): Flow<NotificationTeaser> =
        localDataSource.getNotificationTeaserFlow()

    override suspend fun closeTeaser(shouldNotShowAgain: Boolean) {
        localDataSource.closeTeaser(shouldNotShowAgain)
    }
}
