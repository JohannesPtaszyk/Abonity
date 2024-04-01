package dev.pott.abonity.feature.settings.main

import dev.pott.abonity.core.entity.settings.Settings

data class SettingsState(
    val settings: Settings? = null,
    val privacyPolicyUrl: String = "",
    val imprintUrl: String = "",
)
