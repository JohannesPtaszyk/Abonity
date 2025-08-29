package dev.pott.abonity.core.domain.notification.entities

import dev.pott.abonity.core.entity.notification.NotificationTeaser
import kotlinx.datetime.LocalDateTime

fun createTestNotificationTeaser(
    lastClosed: LocalDateTime = LocalDateTime(
        year = 1,
        month = 1,
        day = 1,
        hour = 1,
        minute = 1,
        second = 1,
        nanosecond = 1,
    ),
    shouldNotShowAgain: Boolean = false,
): NotificationTeaser =
    NotificationTeaser(
        lastClosed = lastClosed,
        shouldNotShowAgain = shouldNotShowAgain,
    )
