package dev.pott.abonity.core.local.notification

import androidx.datastore.core.DataStore
import dev.pott.abonity.core.domain.notification.NotificationTeaserLocalDataSource
import dev.pott.abonity.core.entity.notification.NotificationTeaser
import dev.pott.abonity.core.local.notification.datastore.entities.NotificationTeaserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class NotificationTeaserDataStoreDataSource @Inject constructor(
    private val clock: Clock,
    private val dataStore: DataStore<NotificationTeaserEntity>,
) : NotificationTeaserLocalDataSource {

    override fun getNotificationTeaserFlow(): Flow<NotificationTeaser> =
        dataStore.data.map { entity ->
            val lastClosed = entity.lastClosedLocalDateTime?.let {
                val storedTimeZone = TimeZone.of(entity.timeZoneId!!)
                val dateTime = LocalDateTime.parse(it)
                dateTime.toInstant(storedTimeZone)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            }
            NotificationTeaser(
                lastClosed = lastClosed,
                entity.shouldNotShowAgain,
            )
        }

    override suspend fun closeTeaser(shouldNotShowAgain: Boolean) {
        dataStore.updateData {
            val timeZone = TimeZone.currentSystemDefault()
            NotificationTeaserEntity(
                lastClosedLocalDateTime = clock.now().toLocalDateTime(timeZone).toString(),
                timeZoneId = timeZone.id,
                shouldNotShowAgain = shouldNotShowAgain,
            )
        }
    }
}
