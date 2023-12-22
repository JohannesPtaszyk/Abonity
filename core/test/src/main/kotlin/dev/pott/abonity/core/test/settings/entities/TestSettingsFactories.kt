package dev.pott.abonity.core.test.settings.entities

import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod

fun createTestSettings(
    theme: Theme = Theme.FOLLOW_SYSTEM,
    period: PaymentPeriod = PaymentPeriod.MONTHS,
    enableAdaptiveColors: Boolean = false,
): Settings =
    Settings(
        period = period,
        theme = theme,
        enableAdaptiveColors = enableAdaptiveColors,
    )
