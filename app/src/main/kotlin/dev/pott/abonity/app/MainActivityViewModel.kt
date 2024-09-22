package dev.pott.abonity.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.legal.usecase.ShouldShowTrackingConsentUseCase
import dev.pott.abonity.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    shouldShowTrackingConsent: ShouldShowTrackingConsentUseCase,
) : ViewModel() {

    private val consentConsumed = MutableStateFlow(false)

    val state = combine(
        settingsRepository.getSettingsFlow(),
        shouldShowTrackingConsent(),
        consentConsumed,
    ) { settings, shouldShowConsent, consentConsumed ->
        MainState.Loaded(
            theme = settings.theme,
            adaptiveColorsEnabled = settings.enableAdaptiveColors,
            showConsent = shouldShowConsent && !consentConsumed,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MainState.Loading,
    )

    fun closeConsent() {
        consentConsumed.value = true
    }
}
