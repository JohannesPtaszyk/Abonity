package dev.pott.abonity.core.test.settings

import dev.pott.abonity.core.domain.settings.SettingsLocalDataSource
import dev.pott.abonity.core.entity.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsLocalDataSource(initialSettings: Settings) : SettingsLocalDataSource {

    private val settingsFlow = MutableStateFlow(initialSettings)

    override fun getSettingsFlow(): Flow<Settings> = settingsFlow

    override suspend fun updateSettings(settings: Settings) {
        settingsFlow.value = settings
    }
}
