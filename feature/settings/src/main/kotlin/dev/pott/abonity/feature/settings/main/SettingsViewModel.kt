package dev.pott.abonity.feature.settings.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.config.ConfigRepository
import dev.pott.abonity.core.domain.settings.SettingsRepository
import dev.pott.abonity.core.entity.settings.Settings
import dev.pott.abonity.core.entity.settings.Theme
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    configRepositoryImpl: ConfigRepository,
) : ViewModel() {

    private var updateJob: Job? = null

    val state = combine(
        settingsRepository.getSettingsFlow(),
        configRepositoryImpl.getConfig(),
    ) { settings, config ->
        SettingsState(
            settings,
            config.legal.privacyPolicyUrl,
            config.legal.imprintUrl,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsState(),
    )

    fun setTheme(theme: Theme) {
        updateSettings { it.copy(theme = theme) }
    }

    fun setPeriod(period: PaymentPeriod) {
        updateSettings { it.copy(period = period) }
    }

    fun enableAdaptiveColors(enableAdaptiveColors: Boolean) {
        updateSettings { it.copy(enableAdaptiveColors = enableAdaptiveColors) }
    }

    private fun updateSettings(transform: (Settings) -> Settings) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            val updated = state.value.settings?.let {
                transform(it)
            } ?: return@launch
            settingsRepository.updateSettings(updated)
        }
    }
}
