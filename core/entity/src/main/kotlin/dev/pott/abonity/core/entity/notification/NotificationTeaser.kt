package dev.pott.abonity.core.entity.notification

import kotlinx.datetime.LocalDateTime

data class NotificationTeaser(
    val lastClosed: LocalDateTime?,
    val shouldNotShowAgain: Boolean,
)
