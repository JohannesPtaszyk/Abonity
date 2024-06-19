package dev.pott.abonity.core.entity.settings

import dev.pott.abonity.core.entity.subscription.PaymentPeriod

data class Settings(val period: PaymentPeriod, val theme: Theme, val enableAdaptiveColors: Boolean)
