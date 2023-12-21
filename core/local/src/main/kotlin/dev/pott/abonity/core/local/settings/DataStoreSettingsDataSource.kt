package dev.pott.abonity.core.local.settings

import androidx.datastore.core.DataStore
import dev.pott.abonity.core.domain.settings.SettingsLocalDataSource
import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.local.settings.datastore.entities.LocalTheme
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import dev.pott.abonity.core.local.subscription.toDomain
import dev.pott.abonity.core.local.subscription.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreSettingsDataSource @Inject constructor(
    private val dataStore: DataStore<SettingsEntity>,
) : SettingsLocalDataSource {
    override fun getSettingsFlow(): Flow<Settings> {
        return dataStore.data.map {
            Settings(
                theme = when (it.theme) {
                    LocalTheme.FOLLOW_SYSTEM -> Theme.FOLLOW_SYSTEM
                    LocalTheme.LIGHT -> Theme.LIGHT
                    LocalTheme.DARK -> Theme.DARK
                },
                period = it.period.toDomain(),
            )
        }
    }

    override suspend fun updateSettings(settings: Settings) {
        dataStore.updateData {
            SettingsEntity(
                period = settings.period.toEntity(),
                theme = when (settings.theme) {
                    Theme.FOLLOW_SYSTEM -> LocalTheme.FOLLOW_SYSTEM
                    Theme.LIGHT -> LocalTheme.LIGHT
                    Theme.DARK -> LocalTheme.DARK
                },
            )
        }
    }
}
