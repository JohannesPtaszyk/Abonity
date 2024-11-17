package dev.pott.abonity.feature.legal.consent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentBottomSheet(close: () -> Unit, viewModel: ConsentViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.shouldClose, close) {
        if (state.shouldClose) close()
    }
    ModalBottomSheet(
        onDismissRequest = close,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        ConsentContent(
            onBackClick = viewModel::toggleSecondLayer,
            onToggleConsent = viewModel::toggleService,
            onShowSecondLayer = viewModel::toggleSecondLayer,
            onAcceptAllConsents = viewModel::acceptAll,
            onDenyAllConsents = viewModel::denyAll,
            save = viewModel::save,
            state = state,
        )
    }
}
