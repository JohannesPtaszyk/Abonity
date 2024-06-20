package dev.pott.abonity.core.domain.notification.usecase

import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@VisibleForTesting
const val TIME_UNTIL_SHOWN = 14

class ShouldShowNotificationTeaserUseCase @Inject constructor(
    private val clock: Clock,
    private val repository: NotificationTeaserRepository,
) {

    operator fun invoke(): Flow<Boolean> =
        repository.getNotificationTeaserFlow().map {
            val lastClosed = it.lastClosed
            when {
                it.shouldNotShowAgain -> false
                lastClosed == null -> true
                else -> {
                    val lastClosedInstant = lastClosed.toInstant(TimeZone.currentSystemDefault())
                    val now = clock.now()
                    (now - lastClosedInstant).inWholeDays >= TIME_UNTIL_SHOWN
                }
            }
        }
}
