package dev.pott.abonity.core.local.settings

import androidx.datastore.core.DataStore
import dev.pott.abonity.core.local.settings.datastore.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDataStore(initialSettings: SettingsEntity = SettingsEntity()) :
    DataStore<SettingsEntity> {

    val settingsFlow = MutableStateFlow(initialSettings)

    override val data: Flow<SettingsEntity> = settingsFlow
    override suspend fun updateData(
        transform: suspend (t: SettingsEntity) -> SettingsEntity,
    ): SettingsEntity = transform(settingsFlow.value).also { settingsFlow.value = it }
}
