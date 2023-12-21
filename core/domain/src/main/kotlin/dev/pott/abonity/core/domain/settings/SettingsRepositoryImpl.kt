package dev.pott.abonity.core.domain.settings

import dev.pott.abonity.core.entity.settings.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource,
) : SettingsRepository {
    override fun getSettingsFlow(): Flow<Settings> {
        return settingsLocalDataSource.getSettingsFlow()
    }

    override suspend fun updateSettings(settings: Settings) {
        settingsLocalDataSource.updateSettings(settings)
    }
}
