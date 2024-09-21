package dev.pott.abonity.feature.legal.consent

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.consentGraph(close: () -> Unit, openUrl: (String) -> Unit) {
    composable<ConsentDestination> {
        ConsentScreen(close = close, openUrl = openUrl)
    }
}
