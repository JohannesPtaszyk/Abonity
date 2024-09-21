package dev.pott.abonity.core.local.settings

import androidx.datastore.core.DataStore
import dev.pott.abonity.core.domain.settings.SettingsLocalDataSource
import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.local.settings.datastore.entities.LocalTheme
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import dev.pott.abonity.core.local.subscription.mapper.toDomain
import dev.pott.abonity.core.local.subscription.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStoreDataSource @Inject constructor(
    private val dataStore: DataStore<SettingsEntity>,
) : SettingsLocalDataSource {
    override fun getSettingsFlow(): Flow<Settings> =
        dataStore.data.map { settingsEntity ->
            toDomain(settingsEntity)
        }

    private fun toDomain(settingsEntity: SettingsEntity) =
        Settings(
            period = settingsEntity.period.toDomain(),
            theme = when (settingsEntity.theme) {
                LocalTheme.FOLLOW_SYSTEM -> Theme.FOLLOW_SYSTEM
                LocalTheme.LIGHT -> Theme.LIGHT
                LocalTheme.DARK -> Theme.DARK
            },
            enableAdaptiveColors = settingsEntity.enableAdaptiveColors,
        )

    override suspend fun updateSettings(settings: Settings) {
        dataStore.updateData {
            toEntity(settings)
        }
    }

    private fun toEntity(settings: Settings) =
        SettingsEntity(
            period = settings.period.toEntity(),
            theme = when (settings.theme) {
                Theme.FOLLOW_SYSTEM -> LocalTheme.FOLLOW_SYSTEM
                Theme.LIGHT -> LocalTheme.LIGHT
                Theme.DARK -> LocalTheme.DARK
            },
            enableAdaptiveColors = settings.enableAdaptiveColors,
        )

    override suspend fun updateSettings(block: (Settings) -> Settings) {
        dataStore.updateData { toEntity(block(toDomain(it))) }
    }
}
