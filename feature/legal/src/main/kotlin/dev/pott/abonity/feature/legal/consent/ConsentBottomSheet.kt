package dev.pott.abonity.feature.legal.consent

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentBottomSheet(close: () -> Unit, viewModel: ConsentViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()
    LaunchedEffect(state.shouldClose, close) {
        if (state.shouldClose) close()
    }
    LaunchedEffect(state.showSecondLayer) {
        if (state.showSecondLayer) {
            bottomSheetState.expand()
        }
    }
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = close,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
    ) {
        ConsentContent(
            onBackClick = viewModel::toggleSecondLayer,
            onToggleConsent = viewModel::toggleService,
            onShowSecondLayer = viewModel::toggleSecondLayer,
            onAcceptAllConsents = viewModel::acceptAll,
            onDenyAllConsents = viewModel::denyAll,
            save = viewModel::save,
            state = state,
            containerColor = Color.Unspecified,
            topBarInsets = WindowInsets(0, 0, 0, 0),
            topBarColor = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
        )
    }
}
