package dev.pott.abonity.core.domain.settings

import dev.pott.abonity.core.entity.settings.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettingsFlow(): Flow<Settings>

    suspend fun updateSettings(settings: Settings)

    suspend fun updateSettings(block: (Settings) -> Settings)
}
