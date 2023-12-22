package dev.pott.abonity.app

import dev.pott.abonity.core.entity.settings.Theme

sealed interface MainState {
    data object Loading : MainState

    data class Success(
        val theme: Theme,
        val adaptiveColorsEnabled: Boolean,
    ) : MainState
}
