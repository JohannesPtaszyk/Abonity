package dev.pott.abonity.feature.legal.consent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConsentScreen(
    close: () -> Unit,
    openUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.shouldClose, close) {
        if (state.shouldClose) close()
    }
    ConsentContent(
        onBackClick = viewModel::toggleSecondLayer,
        onToggleConsent = viewModel::toggleService,
        onShowSecondLayer = viewModel::toggleSecondLayer,
        onAcceptAllConsents = viewModel::acceptAll,
        onDenyAllConsents = viewModel::denyAll,
        save = viewModel::save,
        onOpenPrivacyPolicy = { openUrl(state.privacyPolicyUrl) },
        state = state,
        showSecondLayer = true,
        modifier = modifier,
    )
}
