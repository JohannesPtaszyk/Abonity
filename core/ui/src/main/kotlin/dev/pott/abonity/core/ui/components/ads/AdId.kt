package dev.pott.abonity.core.ui.components.ads

import dev.pott.abonity.core.ui.BuildConfig

enum class AdId(val adId: String) {
    DASHBOARD_BANNER(BuildConfig.DASHBOARD_BANNER),
    DETAILS_BANNER(BuildConfig.DETAILS_BANNER),
    SETTINGS_BANNER(BuildConfig.SETTINGS_BANNER),
}
