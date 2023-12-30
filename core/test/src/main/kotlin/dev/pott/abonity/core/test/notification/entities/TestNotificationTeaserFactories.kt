package dev.pott.abonity.core.test.notification.entities

import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.datetime.LocalDateTime

fun createTestNotificationTeaser(
    lastClosed: LocalDateTime = LocalDateTime(
        year = 1,
        monthNumber = 1,
        dayOfMonth = 1,
        hour = 1,
        minute = 1,
        second = 1,
        nanosecond = 1,
    ),
    shouldNotShowAgain: Boolean = false,
): NotificationTeaser {
    return NotificationTeaser(
        lastClosed = lastClosed,
        shouldNotShowAgain = shouldNotShowAgain,
    )
}
