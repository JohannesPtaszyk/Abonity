package dev.pott.abonity.feature.legal.consent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.entity.legal.ConsentGrant
import dev.pott.abonity.core.entity.legal.ConsentServiceId
import dev.pott.abonity.core.entity.legal.ServiceCategory
import dev.pott.abonity.core.entity.legal.TrackingService
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.navigation.BackButton
import dev.pott.abonity.core.ui.util.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentContent(
    onBackClick: () -> Unit,
    onToggleConsent: (ConsentServiceId) -> Unit,
    onShowSecondLayer: () -> Unit,
    onAcceptAllConsents: () -> Unit,
    onDenyAllConsents: () -> Unit,
    save: () -> Unit,
    state: ConsentState,
    modifier: Modifier = Modifier,
    showSecondLayer: Boolean = state.showSecondLayer,
    containerColor: Color = MaterialTheme.colorScheme.background,
    topBarInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    topBarColor: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        containerColor = containerColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.tracking_consent_dialog_title))
                },
                navigationIcon = {
                    AnimatedVisibility(
                        showSecondLayer,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                    ) {
                        BackButton(onClick = onBackClick)
                    }
                },
                actions = {
                    AnimatedVisibility(
                        showSecondLayer,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                    ) {
                        Button(onClick = save) {
                            Text(
                                text = stringResource(
                                    id = R.string.tracking_consent_dialog_cta_save,
                                ),
                            )
                        }
                    }
                },
                scrollBehavior = topAppBarScrollBehavior,
                windowInsets = topBarInsets,
                colors = topBarColor,
            )
        },
    ) { paddingValues ->
        AnimatedContent(
            label = "Layer Content Transition",
            targetState = showSecondLayer,
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        ) { showSecondLayer ->
            if (showSecondLayer) {
                SecondLayer(
                    modifier = Modifier.fillMaxWidth(),
                    paddingValues = paddingValues,
                    state = state,
                    onToggleConsent = onToggleConsent,
                )
            } else {
                val uriHandler = LocalUriHandler.current
                FirstLayer(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onAcceptAllConsents = onAcceptAllConsents,
                    onDenyAllConsents = onDenyAllConsents,
                    onShowSecondLayer = onShowSecondLayer,
                    onOpenPrivacyPolicy = { uriHandler.openUri(state.privacyPolicyUrl) },
                )
            }
        }
    }
}

@Composable
private fun FirstLayer(
    onAcceptAllConsents: () -> Unit,
    onDenyAllConsents: () -> Unit,
    onShowSecondLayer: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = stringResource(id = R.string.tracking_consent_dialog_description))
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onOpenPrivacyPolicy) {
            Text(
                text = stringResource(id = R.string.tracking_consent_dialog_cta_privacy_policy),
                style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onAcceptAllConsents,
            ) {
                Text(text = stringResource(id = R.string.tracking_consent_dialog_cta_accept))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = onDenyAllConsents,
            ) {
                Text(text = stringResource(id = R.string.tracking_consent_dialog_cta_decline))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowSecondLayer,
        ) {
            Text(text = stringResource(id = R.string.tracking_consent_dialog_cta_settings))
        }
    }
}

@Composable
private fun SecondLayer(
    paddingValues: PaddingValues,
    state: ConsentState,
    onToggleConsent: (ConsentServiceId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues + PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            val uriHandler = LocalUriHandler.current
            TextButton(onClick = { uriHandler.openUri(state.privacyPolicyUrl) }) {
                Text(
                    text = stringResource(
                        id = R.string.tracking_consent_dialog_second_layer_cta_privacy_policy,
                    ),
                    style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
                )
            }
        }
        items(state.consents.size) {
            val item = state.consents.entries.elementAt(it)
            ConsentItem(
                service = item.key,
                granted = item.value == ConsentGrant.GRANTED,
                onToggle = { onToggleConsent(item.key.serviceId) },
            )
        }
    }
}

@Composable
fun ConsentItem(
    service: TrackingService,
    granted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Switch(
            checked = granted || service.serviceCategory == ServiceCategory.REQUIRED,
            onCheckedChange = { onToggle() },
            enabled = service.serviceCategory != ServiceCategory.REQUIRED,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            val categoryRes = getCategoryRes(service)
            Text(
                text = stringResource(id = categoryRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            val nameRes = getNameRes(service)
            Text(
                text = stringResource(id = nameRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            val descriptionRes = getDescriptionRes(service)
            Text(text = stringResource(id = descriptionRes))
        }
    }
}

@Composable
private fun getDescriptionRes(service: TrackingService) =
    when (service) {
        TrackingService.FIREBASE_ANALYTICS -> {
            R.string.tracking_service_firebase_analytics_description
        }

        TrackingService.FIREBASE_CRASHLYTICS -> {
            R.string.tracking_service_firebase_crashlytics_description
        }

        TrackingService.FIREBASE_PERFORMANCE -> {
            R.string.tracking_service_firebase_performance_description
        }

        TrackingService.FIREBASE_CLOUD_MESSAGING -> {
            R.string.tracking_service_firebase_cloud_messaging_description
        }

        TrackingService.FIREBASE_IN_APP_MESSAGING -> {
            R.string.tracking_service_firebase_in_app_messaging_description
        }

        TrackingService.FIREBASE_INSTALLATIONS -> {
            R.string.tracking_service_firebase_installations_description
        }

        TrackingService.FIREBASE_REMOTE_CONFIG -> {
            R.string.tracking_service_firebase_remote_config_description
        }
    }

@Composable
private fun getNameRes(service: TrackingService) =
    when (service) {
        TrackingService.FIREBASE_ANALYTICS -> {
            R.string.tracking_service_firebase_analytics
        }

        TrackingService.FIREBASE_CRASHLYTICS -> {
            R.string.tracking_service_firebase_crashlytics
        }

        TrackingService.FIREBASE_PERFORMANCE -> {
            R.string.tracking_service_firebase_performance
        }

        TrackingService.FIREBASE_CLOUD_MESSAGING -> {
            R.string.tracking_service_firebase_cloud_messaging
        }

        TrackingService.FIREBASE_IN_APP_MESSAGING -> {
            R.string.tracking_service_firebase_in_app_messaging
        }

        TrackingService.FIREBASE_INSTALLATIONS -> {
            R.string.tracking_service_firebase_installations
        }

        TrackingService.FIREBASE_REMOTE_CONFIG -> {
            R.string.tracking_service_firebase_remote_config
        }
    }

@Composable
private fun getCategoryRes(service: TrackingService) =
    when (service.serviceCategory) {
        ServiceCategory.ANALYTICS -> R.string.tracking_service_category_analytics
        ServiceCategory.MONITORING -> R.string.tracking_service_category_monitoring
        ServiceCategory.REQUIRED -> R.string.tracking_service_category_required
    }
