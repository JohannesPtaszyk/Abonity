package dev.pott.abonity.core.test.settings

import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.entity.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository(initialSettings: Settings) : SettingsRepository {

    private val settingsFlow = MutableStateFlow(initialSettings)

    override fun getSettingsFlow(): Flow<Settings> = settingsFlow

    override suspend fun updateSettings(settings: Settings) {
        settingsFlow.value = settings
    }

    override suspend fun updateSettings(block: (Settings) -> Settings) {
        settingsFlow.update(block)
    }
}
